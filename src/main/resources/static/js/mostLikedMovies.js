class MoviePagination {
    constructor(config = {}) {
        this.apiEndpoint = config.apiEndpoint || `${API_BASE_URL}/movies/getMostLikedMovies`;
        this.containerSelector = config.containerSelector || '.mostLikedMovies-grid';
        this.paginationSelector = config.paginationSelector || '.pagination-container';
        this.currentPage = 0;
        this.pageSize = config.pageSize || 15;
        this.currentGenre = null;
        this.totalPages = 0;
        this.isLoading = false;
        
        this.init();
    }
/*
	init() {
	    const urlParams = new URLSearchParams(window.location.search);
	    const page = parseInt(urlParams.get('page')) || 0;
	    const genre = urlParams.get('genre') || null;

	    this.createPaginationContainer();

	    this.currentPage = page;
	    this.currentGenre = genre;

	    // Kategori aktiflik kontrolü (güvenli şekilde karşılaştırma)
	    document.querySelectorAll('.category-link').forEach(link => {
	        const linkGenre = link.dataset.genre?.trim().toLowerCase();
	        const selectedGenre = genre?.trim().toLowerCase();
	        if (linkGenre && linkGenre === selectedGenre) {
	            link.classList.add('active');
	        } else {
	            link.classList.remove('active');
	        }
	    });

	    this.loadMovies(page, genre);

	    const categoryList = document.querySelector('.category-list');
	    if (categoryList) {
	        categoryList.addEventListener('click', (e) => {
	            const target = e.target.closest('.category-link');
	            if (target) {
	                e.preventDefault();
	                const selectedGenre = target.dataset.genre?.trim();
	                if (selectedGenre) {
	                    this.selectCategory(target, selectedGenre);
	                }
	            }
	        });
	    }
	}

	
*/

init() {
    const urlParams = new URLSearchParams(window.location.search);
    const page = parseInt(urlParams.get('page')) || 0;
    const genre = urlParams.get('genre') || null;

    this.createPaginationContainer();

    this.currentPage = page;
    this.currentGenre = genre;

    // Kategori aktiflik kontrolü (hem desktop hem mobile için)
    this.updateCategoryActive(genre);

    this.loadMovies(page, genre);

    // Desktop kategori menüsü event listener
    const categoryList = document.querySelector('.category-list');
    if (categoryList) {
        categoryList.addEventListener('click', (e) => {
            const target = e.target.closest('.category-link');
            if (target) {
                e.preventDefault();
                const selectedGenre = target.dataset.genre?.trim();
                if (selectedGenre) {
                    this.selectCategory(target, selectedGenre);
                }
            }
        });
    }

    // Mobile kategori menüsü event listener
    const mobileCategoryList = document.querySelector('.mobile-category-list');
    if (mobileCategoryList) {
        mobileCategoryList.addEventListener('click', (e) => {
            const target = e.target.closest('.mobile-category-link');
            if (target) {
                e.preventDefault();
                const selectedGenre = target.dataset.genre?.trim();
                if (selectedGenre) {
                    this.selectMobileCategory(target, selectedGenre);
                }
            }
        });
    }
}


// Yeni metod: Her iki menüdeki active durumları güncelle
updateCategoryActive(genre) {
    // Desktop kategori linklerini güncelle
    document.querySelectorAll('.category-link').forEach(link => {
        const linkGenre = link.dataset.genre?.trim().toLowerCase();
        const selectedGenre = genre?.trim().toLowerCase();
        if (linkGenre && linkGenre === selectedGenre) {
            link.classList.add('active');
        } else {
            link.classList.remove('active');
        }
    });

    // Mobile kategori linklerini güncelle
    document.querySelectorAll('.mobile-category-link').forEach(link => {
        const linkGenre = link.dataset.genre?.trim().toLowerCase();
        const selectedGenre = genre?.trim().toLowerCase();
        if (linkGenre && linkGenre === selectedGenre) {
            link.classList.add('active');
        } else {
            link.classList.remove('active');
        }
    });
}

// Yeni metod: Mobile kategori seçimi
selectMobileCategory(element, genre) {
    const isActive = element.classList.contains('active');

    if (isActive) {
        // İptal et: Tüm linklerden active kaldır
        this.updateCategoryActive(null);
        this.currentGenre = null;
        this.loadMovies(0, null);
        this.updateUrlParams(0, null);
    } else {
        // Yeni kategori seçildi
        this.updateCategoryActive(genre);
        this.currentGenre = genre;
        this.loadMovies(0, genre);
        this.updateUrlParams(0, genre);
    }

    // Mobile menüyü kapat
    const offcanvas = bootstrap.Offcanvas.getInstance(document.getElementById('categoryOffcanvas'));
    if (offcanvas) {
        offcanvas.hide();
    }
}

selectCategory(element, genre) {
    const isActive = element.classList.contains('active');

    if (isActive) {
        // İptal et: Tüm linklerden active kaldır
        this.updateCategoryActive(null);
        this.currentGenre = null;
        this.loadMovies(0, null);
        this.updateUrlParams(0, null);
    } else {
        // Yeni kategori seçildi
        this.updateCategoryActive(genre);
        this.currentGenre = genre;
        this.loadMovies(0, genre);
        this.updateUrlParams(0, genre);
    }
}



    createPaginationContainer() {
        const container = document.querySelector(this.containerSelector);
        if (!container) {
            console.error('Container not found:', this.containerSelector);
            return;
        }

        // Sayfalama container'ı yoksa oluştur
        if (!document.querySelector(this.paginationSelector)) {
            const paginationDiv = document.createElement('div');
            paginationDiv.className = 'pagination-container mt-4 d-flex justify-content-center';
            container.parentNode.insertBefore(paginationDiv, container.nextSibling);
        }
    }
	
	/*
	selectCategory(element, genre) {
	    const isActive = element.classList.contains('active');

	    if (isActive) {
	      // İptal et: Tüm linklerden active kaldır
	      document.querySelectorAll('.category-link').forEach(link => {
	        link.classList.remove('active');
	      });
	      this.currentGenre = null;
	      this.loadMovies(0, null); // filtreyi kaldır
		  this.updateUrlParams(0,null);
	    } else {
	      // Yeni kategori seçildi
	      document.querySelectorAll('.category-link').forEach(link => {
	        link.classList.remove('active');
	      });
	      element.classList.add('active');
	      this.currentGenre = genre;
	      this.loadMovies(0, genre);
		  this.updateUrlParams(0,genre);
	    }
	  }
	  
	  */
	
	  updateUrlParams(page, genre) {
	      const url = new URL(window.location);
	      url.searchParams.set('page', page);
	      if (genre) {
	          url.searchParams.set('genre', genre);
	      } else {
	          url.searchParams.delete('genre');
	      }
	      window.history.pushState({}, '', url);
	  }

	 
    async loadMovies(page = 0, genre = null) {
		
        if (this.isLoading) return;
        this.updateUrlParams(page,genre);
        this.isLoading = true;
        this.showLoading();

        try {
            const params = new URLSearchParams({
                page: page.toString(),
                size: this.pageSize.toString()
            });

            if (genre && genre.trim() !== '') {
                params.append('genre', genre);
            }

            const response = await fetch(`${this.apiEndpoint}?${params}`);
            
            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }

            const data = await response.json();
            
            this.currentPage = data.number;
            this.totalPages = data.totalPages;
            this.currentGenre = genre;

            this.renderMovies(data.content);
            this.renderPagination(data);
			
			this.updateUrlParams(this.currentPage, this.currentGenre);


        } catch (error) {
            console.error('Film yüklenirken hata oluştu:', error);
            this.showError('Filmler yüklenirken bir hata oluştu. Lütfen daha sonra tekrar deneyin.');
        } finally {
            this.isLoading = false;
        }
    }

    renderMovies(movies) {

        const container = document.querySelector(this.containerSelector);
        if (!container) return;

        container.innerHTML = '';

        if (!movies || movies.length === 0) {
            container.innerHTML = `
                <div class="col-12 text-center">
                    <div class="alert alert-info">
                        <i class="bi bi-info-circle me-2"></i>
                        Henüz film bulunamadı.
                    </div>
                </div>
            `;
            return;
        }

        movies.forEach(movie => {
            const movieCard = this.createMovieCard(movie);
            container.appendChild(movieCard);
        });

        // Kartlar yüklenince animasyon efekti
        this.animateCards();
    }

    createMovieCard(movie) {
        console.log(movie);
        const cardDiv = document.createElement('div');
        cardDiv.className = 'col-sm-12 col-md-6 col-lg-4 movie-card-wrapper';
        
        const genreBadges = movie.genres ? 
            movie.genres.map(genre => `<span class="badge bg-secondary">${genre.name || genre}</span>`).join('') : '';

        const recommendedBy = movie.recommendedBy ? `
            <div class="recommended-by mb-2">
            <small class="text-muted">
            <i class="bi bi-person-circle"></i>
            Öneren: <span class="text-white">${movie.recommendedBy.userName || 'Bilinmiyor'}</span>
			<span class="badge ms-1 tag-${movie.recommendedBy?.tag?.toLowerCase().replace(' ', '-') || 'guest'}">
						   ${movie.recommendedBy?.tag || 'Ziyaretçi'}
						 </span>
            </small>
            </div>
            ` : '';

        const likeButtonClass = movie.likedByUser ? 'btn-like active' : 'btn-like';
        const dislikeButtonClass = movie.dislikedByUser ? 'btn-dislike active' : 'btn-dislike';

        cardDiv.innerHTML = `
            <div class="card movie-card" data-movie-id="${movie.id}">
                <div class="card-img-wrapper">
                <a href="/film-detay?id=${movie.id}" class="text-decoration-none">
                    <img src="${movie.posterUrl || '/images/no-poster.jpg'}" 
                         class="card-img-top" 
                         alt="${movie.movieName}"
                         onerror="this.src='/images/no-poster.jpg'">
                </a>
                    <!-- Sağ üst liste butonu -->
                    <button class="btn-add-list" onclick="moviePagination.addToList(${movie.id})">
                        <i class="bi bi-plus"></i>
                    </button>
                    
                    <!-- Sol üst puan -->
                    <div class="movie-rating">
                        <i class="bi bi-star-fill"></i>
                        <span>${movie.voteAverage ? movie.voteAverage.toFixed(1) : '0.0'}</span>
                    </div>
                </div>
                
                <div class="card-body">
                <a href="movie.html?id=${movie.id}" class="text-decoration-none">
                    <h5 class="card-title text-white fw-bold text-truncate">${movie.movieName}</h5>
                 </a>   
                    <!-- Tür badge'leri -->
                    <div class="movie-genres mb-2 text-truncate">
                        ${genreBadges}
                    </div>
                    
                    ${recommendedBy}
                    
                    <!-- İstatistikler -->
                    <div class="movie-stats">
                        <!-- Normal durumdaki istatistikler -->
                        <div class="stats-content pt-2">
                            <div class="d-flex justify-content-between text-muted small">
                                <span>
                                    <i class="bi bi-eye"></i> ${movie.totalViews || '0'} Görüntüleme
                                </span>
                                <span>
                                    <i class="bi bi-chat"></i> 45 Yorum
                                </span>
                            </div>
                        </div>
                        
                        <!-- Hover durumunda çıkacak butonlar -->
                        <div class="like-dislike-buttons">
                        <button id="like-btn-${movie.id}" data-movie-id="${movie.id}" class="mt-1 btn-like ${movie.likedByUser ? 'active' : ''}" onclick="toggleLike(${movie.id},true)">
                            <i class="bi bi-hand-thumbs-up"></i>
                            <span class="like-count">${movie.likes}</span>
                        </button>
                        <button id="dislike-btn-${movie.id}" data-movie-id="${movie.id}" class="mt-1 btn-dislike ${movie.dislikedByUser ? 'active' : ''}" onclick="toggleLike(${movie.id},false)">
                            <i class="bi bi-hand-thumbs-down"></i>
                            <span class="dislike-count">${movie.dislikes}</span>
                        </button>
                        </div>
                        </div>
                    </div>
                </div>
            </div>
        `;

        return cardDiv;
    }

    renderPagination(pageData) {
        const paginationContainer = document.querySelector(this.paginationSelector);
        if (!paginationContainer) return;

        if (pageData.totalPages <= 1) {
            paginationContainer.innerHTML = '';
            return;
        }

        const pagination = document.createElement('nav');
        pagination.innerHTML = `
            <ul class="pagination pagination-lg">
                ${this.createPaginationItems(pageData)}
            </ul>
        `;

        paginationContainer.innerHTML = '';
        paginationContainer.appendChild(pagination);
    }

    createPaginationItems(pageData) {
        const currentPage = pageData.number;
        const totalPages = pageData.totalPages;
        let items = '';

        // Önceki sayfa butonu
        items += `
            <li class="page-item ${currentPage === 0 ? 'disabled' : ''}">
                <a class="page-link" href="#" onclick="moviePagination.goToPage(${currentPage - 1}); return false;">
                    <i class="bi bi-chevron-left"></i> Önceki
                </a>
            </li>
        `;

        // Sayfa numaraları
        const startPage = Math.max(0, currentPage - 2);
        const endPage = Math.min(totalPages - 1, currentPage + 2);

        // İlk sayfa
        if (startPage > 0) {
            items += `
                <li class="page-item">
                    <a class="page-link" href="#" onclick="moviePagination.goToPage(0); return false;">1</a>
                </li>
            `;
            if (startPage > 1) {
                items += '<li class="page-item disabled"><span class="page-link">...</span></li>';
            }
        }

        // Orta sayfalar
        for (let i = startPage; i <= endPage; i++) {
            items += `
                <li class="page-item ${i === currentPage ? 'active' : ''}">
                    <a class="page-link" href="#" onclick="moviePagination.goToPage(${i}); return false;">${i + 1}</a>
                </li>
            `;
        }

        // Son sayfa
        if (endPage < totalPages - 1) {
            if (endPage < totalPages - 2) {
                items += '<li class="page-item disabled"><span class="page-link">...</span></li>';
            }
            items += `
                <li class="page-item">
                    <a class="page-link" href="#" onclick="moviePagination.goToPage(${totalPages - 1}); return false;">${totalPages}</a>
                </li>
            `;
        }

        // Sonraki sayfa butonu
        items += `
            <li class="page-item ${currentPage === totalPages - 1 ? 'disabled' : ''}">
                <a class="page-link" href="#" onclick="moviePagination.goToPage(${currentPage + 1}); return false;">
                    Sonraki <i class="bi bi-chevron-right"></i>
                </a>
            </li>
        `;

        return items;
    }

    goToPage(pageNumber) {
        if (pageNumber < 0 || pageNumber >= this.totalPages || pageNumber === this.currentPage) {
            return;
        }
        
        this.loadMovies(pageNumber, this.currentGenre);
		this.updateUrlParams(pageNumber,this.currentGenre);
        
        // Sayfanın üstüne kaydır
        const container = document.querySelector(this.containerSelector);
        if (container) {
            container.scrollIntoView({ behavior: 'smooth', block: 'start' });
        }
    }

    filterByGenre(genre) {
        this.currentPage = 0;
        this.loadMovies(0, genre);
    }

    showLoading() {
        const container = document.querySelector(this.containerSelector);
        if (!container) return;

        container.innerHTML = `
            <div class="col-12 text-center">
                <div class="d-flex justify-content-center align-items-center" style="height: 200px;">
                    <div class="spinner-border text-primary" role="status">
                        <span class="visually-hidden">Yükleniyor...</span>
                    </div>
                    <span class="ms-3 text-white">Filmler yükleniyor...</span>
                </div>
            </div>
        `;
    }

    showError(message) {
        const container = document.querySelector(this.containerSelector);
        if (!container) return;

        container.innerHTML = `
            <div class="col-12">
                <div class="alert alert-danger">
                    <i class="bi bi-exclamation-triangle me-2"></i>
                    ${message}
                    <button class="btn btn-outline-danger btn-sm ms-3" onclick="moviePagination.loadMovies()">
                        Tekrar Dene
                    </button>
                </div>
            </div>
        `;
    }

    animateCards() {
        const cards = document.querySelectorAll('.movie-card-wrapper');
        cards.forEach((card, index) => {
            card.style.opacity = '0';
            card.style.transform = 'translateY(20px)';
            
            setTimeout(() => {
                card.style.transition = 'all 0.3s ease';
                card.style.opacity = '1';
                card.style.transform = 'translateY(0)';
            }, index * 100);
        });
    }

    async addToList(movieId) {
        try {
            const response = await fetch(`/api/movies/${movieId}/add-to-list`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                }
            });

            if (response.ok) {
                // Başarı bildirimi göster
                this.showNotification('Film listenize eklendi!', 'success');
            }
        } catch (error) {
            console.error('Listeye ekleme başarısız:', error);
            this.showNotification('Film listeye eklenirken hata oluştu!', 'error');
        }
    }

    showNotification(message, type = 'info') {
        const notification = document.createElement('div');
        notification.className = `alert alert-${type === 'success' ? 'success' : 'danger'} position-fixed`;
        notification.style.cssText = 'top: 20px; right: 20px; z-index: 9999; min-width: 300px;';
        notification.innerHTML = `
            <i class="bi bi-${type === 'success' ? 'check-circle' : 'exclamation-circle'} me-2"></i>
            ${message}
        `;

        document.body.appendChild(notification);

        setTimeout(() => {
            notification.remove();
        }, 3000);
    }

    refresh() {
        this.loadMovies(this.currentPage, this.currentGenre);
    }
}


function toggleLike(movieId, isLike) {
  const likeButton = document.querySelector(`#like-btn-${movieId}`);
  const dislikeButton = document.querySelector(`#dislike-btn-${movieId}`);
  const likeCountElement = document.querySelector(`#like-btn-${movieId} .like-count`);
  const dislikeCountElement = document.querySelector(`#dislike-btn-${movieId} .dislike-count`);
  
  // Mevcut durumu al
  const wasLiked = likeButton.classList.contains('active');
  const wasDisliked = dislikeButton.classList.contains('active');
  
  // Mevcut sayıları al
  let likeCount = parseInt(likeCountElement.textContent);
  let dislikeCount = parseInt(dislikeCountElement.textContent);
  
  // Frontend'te hemen güncelle
  if (isLike) {
    // Like butonuna basıldı
    if (wasLiked) {
      // Zaten beğenilmişse, beğeniyi kaldır
      likeButton.classList.remove('active');
      likeCount--;
    } else {
      // Beğenilmemişse, beğen
      likeButton.classList.add('active');
      likeCount++;
      
      // Eğer dislike aktifse, onu kaldır
      if (wasDisliked) {
        dislikeButton.classList.remove('active');
        dislikeCount--;
      }
    }
  } else {
    // Dislike butonuna basıldı
    if (wasDisliked) {
      // Zaten beğenilmemişse, beğenmemeyi kaldır
      dislikeButton.classList.remove('active');
      dislikeCount--;
    } else {
      // Beğenilmemişse, beğenme
      dislikeButton.classList.add('active');
      dislikeCount++;
      
      // Eğer like aktifse, onu kaldır
      if (wasLiked) {
        likeButton.classList.remove('active');
        likeCount--;
      }
    }
  }
  
  // Sayıları güncelle
  likeCountElement.textContent = likeCount;
  dislikeCountElement.textContent = dislikeCount;
  
  // Backend'e istek gönder (arka planda)
  fetch(`${API_BASE_URL}/movies/${movieId}/${isLike ? 'like' : 'dislike'}`, {
    method: 'POST',
    credentials: 'include',
  })
  .then(res => {
    if (!res.ok) throw new Error('İstek başarısız');
    return res.json();
  })
  .then(data => {
    // Backend'den gelen gerçek verilerle senkronize et (isteğe bağlı)
    likeCountElement.textContent = data.likes;
    dislikeCountElement.textContent = data.dislikes;
    
    // Buton durumlarını da senkronize et
    if (data.likedByUser) {
      likeButton.classList.add('active');
      dislikeButton.classList.remove('active');
    } else if (data.dislikedByUser) {
      dislikeButton.classList.add('active');
      likeButton.classList.remove('active');
    } else {
      likeButton.classList.remove('active');
      dislikeButton.classList.remove('active');
    }
  })
  .catch(error => {
    console.error('Error:', error);
    // Hata durumunda frontend'i geri al (rollback)
    // Bu kısmı isteğe bağlı olarak ekleyebilirsiniz
  });
}

// Global instance oluştur
let moviePagination;

// Sayfa yüklendiğinde başlat
document.addEventListener('DOMContentLoaded', function() {
    moviePagination = new MoviePagination({
        apiEndpoint: `${API_BASE_URL}/movies/getMostLikedMovies`, // API endpoint'inizi buraya yazın
        containerSelector: '.mostLikedMovies-grid',
        paginationSelector: '.pagination-container',
        pageSize: 15
    });
});

// Export for module usage
if (typeof module !== 'undefined' && module.exports) {
    module.exports = MoviePagination;
}