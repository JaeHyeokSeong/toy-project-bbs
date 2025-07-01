$(document).ready(function () {
    // 1) 폼 및 히든필드 요소 캐싱
    const form = document.getElementById('postForm');
    const titleInput = document.getElementById('title');
    const submitBtn = document.getElementById('submit');
    const hiddenContent = document.getElementById('hidden-content');
    const storeFileNamesContainer = document.getElementById('storeFileNamesContainer');

    // 2) 업로드된 파일명들을 저장할 배열
    const storeFileNames = [];

    // 3) 이미지 업로드 + 에디터 삽입 공통 로직
    function uploadImage(file) {
        // 최대 20개 제한
        if (storeFileNames.length >= 20) {
            alert('이미지는 최대 20개까지만 업로드할 수 있습니다.');
            return;
        }

        const formData = new FormData();
        formData.append('multipartFile', file);

        fetch('/api/image', { method: 'POST', body: formData })
            .then(res => {
                if (!res.ok) {
                    if (res.status === 413) {
                        alert('파일의 용량이 너무 큽니다.');
                    } else {
                        alert(`이미지 업로드에 실패했습니다. (상태코드: ${res.status})`);
                    }
                    throw new Error(`Upload failed with status ${res.status}`);
                }
                return res.json();
            })
            .then(dto => {
                // 삽입 위치 구하고 서버 URL로 이미지 삽입
                const url = '/api/images/' + dto.storeFileName;
                const range = quill.getSelection(true);
                quill.insertEmbed(range.index, 'image', url);
                quill.setSelection(range.index + 1);
                // 배열에 파일명 기록
                storeFileNames.push(dto.storeFileName);
            })
            .catch(err => console.error('Image upload failed', err));
    }

    // 4) Quill 에디터 초기화
    const quill = new Quill('#editor', {
        modules: {
            toolbar: {
                container: '#toolbar',
                handlers: { image: imageHandler }
            }
        },
        placeholder: '서로 예의를 지키며 존중하는 문화를 만들가요.',
        theme: 'snow'
    });

    // 5) 툴바 이미지 버튼 핸들러
    function imageHandler() {
        const input = document.createElement('input');
        input.type = 'file';
        input.accept = 'image/*';
        input.click();
        input.onchange = () => {
            const file = input.files[0];
            if (file) uploadImage(file);
        };
    }

    // 6) Base64 인라인 삽입 막기 (캡처 단계)
    const container = quill.root.parentNode; // .ql-container
    ['dragover', 'drop', 'past'].forEach(evt =>
        container.addEventListener(evt, e => {
            if (e.dataTransfer && e.dataTransfer.types.includes('Files')) {
                e.preventDefault();
                e.stopPropagation();
            }
        }, { capture: true })
    );

    // 7) 이미지 제거 시 DELETE 호출 및 배열 정리
    quill.on('text-change', function () {
        const currentNames = Array.from(quill.root.querySelectorAll('img'))
            .map(img => img.getAttribute('src').split('/').pop());

        storeFileNames.slice().forEach(name => {
            if (!currentNames.includes(name)) {
                fetch(`/api/image/${name}`, { method: 'DELETE' })
                    .catch(err => console.error('Image delete failed', err));
                const idx = storeFileNames.indexOf(name);
                if (idx > -1) storeFileNames.splice(idx, 1);
            }
        });
    });

    // 8) 폼 제출 전 히든 필드 업데이트
    form.addEventListener('submit', function () {
        // 에디터 내용
        hiddenContent.value = quill.root.innerHTML;
        // 파일명 히든 필드 생성
        storeFileNamesContainer.innerHTML = '';
        storeFileNames.forEach(name => {
            const input = document.createElement('input');
            input.type = 'hidden';
            input.name = 'storeFileNames';
            input.value = name;
            storeFileNamesContainer.appendChild(input);
        });
    });

    submitBtn.disabled = true;  // 초기 상태

    function validateForm() {
        const titleFilled = titleInput.value.trim().length > 0 && titleInput.value.trim().length <= 100;
        const contentFilled = quill.getText().trim().length > 0;
        submitBtn.disabled = !(titleFilled && contentFilled);
    }

    // 제목 입력 변화 감지
    titleInput.addEventListener('input', validateForm);
    // 에디터 내용 변화 감지
    quill.on('text-change', validateForm);
    // 초기 검증
    validateForm();
});