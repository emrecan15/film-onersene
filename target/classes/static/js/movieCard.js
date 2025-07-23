// movieCard.js for allmovies page


function createMovieCard(movie) {
  return `
    <div class="col-sm-6 col-md-6 col-lg-3">
      <div class="card movie-card">
        <div class="card-img-wrapper">
         <a href="/film-detay?id=${movie.id}" class="text-decoration-none">
          <img src="${movie.posterUrl}" class="card-img-top" alt="${movie.movieName}">
          </a>
          <!-- Sağ üst liste butonu -->
          <button class="btn-add-list">
            <i class="bi bi-plus"></i>
          </button>
          <!-- Sol üst puan -->
          <div class="movie-rating">
            <i class="bi bi-star-fill"></i>
            <span>${movie.voteAverage}</span>
          </div>
        </div>
        <div class="card-body">
         <a href="movie.html?id=${movie.id}" class="text-decoration-none">
          <h5 class="card-title text-white text-truncate">${movie.movieName}</h5>
          </a>
          <div class="movie-genres mb-2 text-truncate">
            ${movie.genres.map(genre => `<span class="badge bg-secondary">${genre.name}</span>`).join(' ')}
          </div>
          <div class="recommended-by mb-2">
            <small class="text-muted">
              <i class="bi bi-person-circle"></i>
             Öneren: <span class="text-white">${movie.recommendedBy?.userName || 'Bilinmiyor'}</span>
			 <span class="badge ms-1 tag-${movie.recommendedBy?.tag?.toLowerCase().replace(' ', '-') || 'guest'}">
			   ${movie.recommendedBy?.tag || 'Ziyaretçi'}
			 </span>
             
            </small>
          </div>
          <div class="movie-stats">
            <div class="stats-content pt-2">
              <div class="d-flex justify-content-between text-muted small">
                <span><i class="bi bi-eye"></i> ${movie.totalViews || '0'} görüntüleme</span>
                <span><i class="bi bi-chat"></i> 12 yorum</span>
              </div>
            </div>
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
}

/* this function is working on index.html getLatest7movies */
async function fetchAndRenderMovies() {
  try {
    const response = await fetch(`${API_BASE_URL}/movies/recent`,{credentials: 'include'});
    if (!response.ok) throw new Error(`HTTP error! status: ${response.status}`);

    const movies = await response.json();

    const container = document.querySelector('.movies-grid');
    container.innerHTML = '';

    movies.forEach(movie => {
      container.innerHTML += createMovieCard(movie);
    });

  } catch (error) {
    console.error('Filmler alınamadı:', error);
  }
}

document.addEventListener('DOMContentLoaded', fetchAndRenderMovies);






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
