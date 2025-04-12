/**
 * 소셜오더 애플리케이션 공통 JavaScript
 */
document.addEventListener('DOMContentLoaded', function() {
    // 알림 메시지 자동 닫기
    const alerts = document.querySelectorAll('.alert');
    alerts.forEach(alert => {
        setTimeout(() => {
            const closeBtn = alert.querySelector('.btn-close');
            if (closeBtn) {
                closeBtn.click();
            }
        }, 5000); // 5초 후 자동 닫기
    });

    // 드롭다운 메뉴 개선
    const dropdownMenus = document.querySelectorAll('.dropdown-menu');
    dropdownMenus.forEach(menu => {
        menu.addEventListener('click', (e) => {
            if (e.target.classList.contains('dropdown-item')) {
                e.stopPropagation();
            }
        });
    });

    // 테마 모드 토글 (다크 모드 / 라이트 모드)
    const themeToggle = document.getElementById('themeToggle');
    if (themeToggle) {
        // 저장된 테마 확인
        const savedTheme = localStorage.getItem('theme') || 'light';
        if (savedTheme === 'dark') {
            document.body.classList.add('dark-mode');
            updateThemeToggleButton(true);
        }

        themeToggle.addEventListener('click', () => {
            document.body.classList.toggle('dark-mode');
            const isDarkMode = document.body.classList.contains('dark-mode');

            // 테마 상태 저장
            localStorage.setItem('theme', isDarkMode ? 'dark' : 'light');
            updateThemeToggleButton(isDarkMode);
        });
    }

    // 다크 모드 토글 버튼 업데이트
    function updateThemeToggleButton(isDarkMode) {
        const themeToggle = document.getElementById('themeToggle');
        if (themeToggle) {
            const icon = themeToggle.querySelector('i');
            const text = themeToggle.querySelector('span');

            if (isDarkMode) {
                icon.className = 'bi bi-sun';
                text.textContent = '라이트 모드';
            } else {
                icon.className = 'bi bi-moon-stars';
                text.textContent = '다크 모드';
            }
        }
    }

    // 게시글 이미지 확대 보기
    const postPhotos = document.querySelectorAll('.post-photo');
    postPhotos.forEach(photo => {
        photo.addEventListener('click', () => {
            const imgSrc = photo.getAttribute('src');
            const modal = createImageModal(imgSrc);
            document.body.appendChild(modal);

            // 모달 표시
            const bsModal = new bootstrap.Modal(modal);
            bsModal.show();

            // 모달 닫힘 이벤트
            modal.addEventListener('hidden.bs.modal', () => {
                document.body.removeChild(modal);
            });
        });
    });

    // 이미지 모달 생성 함수
    function createImageModal(imgSrc) {
        const modalDiv = document.createElement('div');
        modalDiv.className = 'modal fade';
        modalDiv.id = 'imageModal';
        modalDiv.tabIndex = '-1';
        modalDiv.setAttribute('aria-labelledby', 'imageModalLabel');
        modalDiv.setAttribute('aria-hidden', 'true');

        modalDiv.innerHTML = `
      <div class="modal-dialog modal-dialog-centered modal-lg">
        <div class="modal-content">
          <div class="modal-header">
            <h5 class="modal-title" id="imageModalLabel">이미지 보기</h5>
            <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
          </div>
          <div class="modal-body text-center">
            <img src="${imgSrc}" class="img-fluid" alt="확대 이미지">
          </div>
        </div>
      </div>
    `;

        return modalDiv;
    }

    // 활성 메뉴 아이템 스크롤 중앙 정렬 (모바일)
    const activeMenuItem = document.querySelector('.navbar-nav .active');
    if (activeMenuItem && window.innerWidth < 768) {
        const navbarNav = document.querySelector('.navbar-nav');
        if (navbarNav) {
            navbarNav.scrollLeft = activeMenuItem.offsetLeft - navbarNav.offsetWidth / 2 + activeMenuItem.offsetWidth / 2;
        }
    }

    // 스크롤 상단 버튼
    const backToTopBtn = document.getElementById('backToTop');
    if (backToTopBtn) {
        window.addEventListener('scroll', () => {
            if (window.scrollY > 300) {
                backToTopBtn.classList.add('show');
            } else {
                backToTopBtn.classList.remove('show');
            }
        });

        backToTopBtn.addEventListener('click', () => {
            window.scrollTo({
                top: 0,
                behavior: 'smooth'
            });
        });
    }
});