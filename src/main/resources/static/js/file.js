$(document).ready(function () {
    // 1) 폼 및 히든필드 요소 캐싱
    const form = document.getElementById('postForm');
    const titleInput = document.getElementById('title');
    const submitBtn = document.getElementById('submit');
    const hiddenContent = document.getElementById('hidden-content');
    const storeFileNamesContainer = document.getElementById('storeFileNamesContainer');
    const deleteFileNamesContainer = document.getElementById('deleteFileNamesContainer');

    // 2) 업로드된 파일명들을 저장할 배열 (신규 업로드 전용)
    const storeFileNames = [];
    // 3) 수정 모드일 때만 사용할, 기존 로드된 파일명 저장
    const existingFileNames = [];
    // 4) 수정 모드에서 삭제할 파일명을 모아둘 배열
    const pendingDelete = [];

    // 5) create vs edit 모드 판별 (URL에 '/edit/' 포함 여부)
    const isEditMode = form.action.includes('/edit/');

    // 6) 이미지 업로드 + 삽입
    function uploadImage(file) {
        if (existingFileNames.length + storeFileNames.length >= 20) {
            alert('이미지는 최대 20개까지만 업로드할 수 있습니다.');
            return;
        }
        const fd = new FormData();
        fd.append('multipartFile', file);
        fetch('/api/file', { method: 'POST', body: fd })
            .then(res => {
                if (!res.ok) {
                    if (res.status === 413) alert('파일 용량이 너무 큽니다.');
                    else alert(`업로드 실패 (상태코드: ${res.status})`);
                    throw new Error(`Upload failed ${res.status}`);
                }
                return res.json();
            })
            .then(dto => {
                const url = '/api/files/' + dto.data.storeFileName;
                const range = quill.getSelection(true);
                quill.insertEmbed(range.index, 'image', url);
                quill.setSelection(range.index + 1);
            })
            .catch(console.error);
    }

    // 7) Quill 초기화
    const BlockEmbed = Quill.import('blots/block/embed');

    class DividerBlot extends BlockEmbed {
        static blotName = 'divider';
        static tagName = 'hr';
    }

    Quill.register(DividerBlot);

    const quill = new Quill('#editor', {
        modules: {
            toolbar: { container: '#toolbar', handlers: { image: imageHandler } }
        },
        theme: 'snow'
    });

    // Quill 생성 후 바로
    const toolbar = quill.getModule('toolbar');
    toolbar.addHandler('divider', function() {
        const range = quill.getSelection(true);
        quill.insertEmbed(range.index, 'divider', true, Quill.sources.USER);
        quill.setSelection(range.index + 1, Quill.sources.SILENT);
    });

    // 8) 수정 모드일 때 기존 컨텐츠 로드 & existingFileNames 채우기
    if (window.board_content) {
        const clean = DOMPurify.sanitize(window.board_content, {ADD_TAGS: ['iframe']});
        quill.clipboard.dangerouslyPasteHTML(clean);
        quill.root.querySelectorAll('img').forEach(img => {
            const name = img.getAttribute('src').split('/').pop();
            if (name && !existingFileNames.includes(name)) {
                existingFileNames.push(name);
            }
        });
    }

    // 9) 툴바 이미지 버튼 핸들러
    function imageHandler() {
        const inp = document.createElement('input');
        inp.type = 'file';
        inp.accept = 'image/*';
        inp.click();
        inp.onchange = () => {
            const file = inp.files[0];
            if (file) uploadImage(file);
        };
    }

    // 10) Base64 인라인 차단 (캡처 단계)
    const container = quill.root.parentNode;
    ['dragover','drop','paste'].forEach(evt =>
        container.addEventListener(evt, e => {
            if ((e.dataTransfer && e.dataTransfer.types.includes('Files')) ||
                (e.clipboardData && Array.from(e.clipboardData.items).some(i=>i.kind==='file'))) {
                e.preventDefault(); e.stopPropagation();
            }
        }, { capture: true })
    );

    // 11) 이미지 삭제 감지
    quill.on('text-change', () => {
        const current = Array.from(quill.root.querySelectorAll('img'))
            .map(img => img.getAttribute('src').split('/').pop());

        current.forEach(name => {
            if (!storeFileNames.includes(name) && !existingFileNames.includes(name)) {
                storeFileNames.push(name);
            }
        });

        // Undo 복구: 에디터에 다시 나타난 이미지는 pendingDelete에서 제거
        pendingDelete.slice().forEach(name => {
            if (current.includes(name)) {
                pendingDelete.splice(pendingDelete.indexOf(name), 1);
                if (!existingFileNames.includes(name)) {
                    existingFileNames.push(name);
                }
            }
        });

        // ── 기존 이미지 삭제 (edit 모드) ──
        existingFileNames.slice().forEach(name => {
            if (!current.includes(name)) {
                pendingDelete.push(name);
                existingFileNames.splice(existingFileNames.indexOf(name), 1);
            }
        });

        // ── 신규 업로드 이미지 삭제 감지 ──
        storeFileNames.slice().forEach(name => {
            if (!current.includes(name)) {
                if (isEditMode) {
                    //수정 모드일 때만 삭제 예약
                    if (!pendingDelete.includes(name)) {
                        pendingDelete.push(name);
                    }
                }
                storeFileNames.splice(storeFileNames.indexOf(name), 1);
            }
        });
    });

    // 12) 폼 제출 전 처리
    form.addEventListener('submit', function () {
        // (1) Quill 내용
        hiddenContent.value = quill.root.innerHTML;

        // (2) storeFileNames 히든 필드 (신규 업로드)
        storeFileNamesContainer.innerHTML = '';
        if (storeFileNames.length) {
            storeFileNames.forEach(name => {
                const inp = document.createElement('input');
                inp.type = 'hidden';
                inp.name = 'storeFileNames';
                inp.value = name;
                storeFileNamesContainer.appendChild(inp);
            });
        } else {
            // 빈 리스트 바인드용 (값 없이 하나라도 보내면 null 대신 빈 리스트로 바인드)
            const inp = document.createElement('input');
            inp.type = 'hidden';
            inp.name = 'storeFileNames';
            inp.value = '';
            storeFileNamesContainer.appendChild(inp);
        }

        // (3) edit 모드에서 삭제할 파일명(deleteFileNames) 전송
        deleteFileNamesContainer.innerHTML = '';
        if (isEditMode) {
            if (pendingDelete.length) {
                pendingDelete.forEach(name => {
                    const inp = document.createElement('input');
                    inp.type = 'hidden'; inp.name = 'deleteFileNames'; inp.value = name;
                    deleteFileNamesContainer.appendChild(inp);
                });
            } else {
                // 빈 리스트 바인드
                const inp = document.createElement('input');
                inp.type = 'hidden'; inp.name = 'deleteFileNames'; inp.value = '';
                deleteFileNamesContainer.appendChild(inp);
            }
        }
    });

    // 13) 폼 검증 & 버튼 활성화
    submitBtn.disabled = true;
    function validate() {
        const okTitle = titleInput.value.trim().length > 0
            && titleInput.value.trim().length <= 100;
        const okContent = quill.getText().trim().length > 0;
        submitBtn.disabled = !(okTitle && okContent);
    }
    titleInput.addEventListener('input', validate);
    quill.on('text-change', validate);
    validate();
});