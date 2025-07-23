// status kontrol

document.addEventListener('DOMContentLoaded', async () => {
    checkAuthStatus();
    ensureGuestId();
    
});



async function checkAuthStatus() {
    try {
        const res = await fetch(`${API_BASE_URL}/auth/status`, {
            method: 'GET',
            credentials: 'include'
        });

        const data = await res.json();

        if (data.authenticated) {
            console.log("✅ Giriş yapılmış, kullanıcı:", data.username);
            buildNavbar(data); // 👈 Navbar'ı güncelle
        } else {
            console.log("🚫 Giriş yapılmamış");
            buildNavbar({ authenticated: false }); // 👈 Çıkış yapılmış UI
        }
        
    } catch (err) {
        console.error("Kullanıcı durumu alınamadı", err);
        buildNavbar({ authenticated: false }); // Hata durumunda
    }
}
/*
function buildNavbar(user) {
  const authDiv = document.getElementById('authButtons');
  if (user.username) {
    authDiv.innerHTML = `
      <span class="text-white me-3">Hoşgeldin, <strong>${user.username}</strong></span>
      <button id="logoutBtn" class="btn btn-outline-light rounded-pill">Çıkış Yap</button>
    `;

    document.getElementById('logoutBtn').addEventListener('click', async () => {
      await fetch(`${API_BASE_URL}/auth/logout`, {
        method: 'POST',
        credentials: 'include',
      });
      // Çıkış sonrası navbarı güncelle
      buildNavbar({ }); 
    });
  } else {
    authDiv.innerHTML = `
      <button class="btn btn-outline-light me-2 rounded-pill" data-bs-toggle="modal" data-bs-target="#authModal" data-tab="login">Giriş Yap</button>
      <button class="btn btn-outline-light rounded-pill" data-bs-toggle="modal" data-bs-target="#authModal" data-tab="register">Üye Ol</button>
    `;
  }
}

*/

function buildNavbar(user) {
    const authDiv = document.getElementById('authButtons');
	
	const profileImageUrl = user.profileImage 
	  || `https://ui-avatars.com/api/?name=${encodeURIComponent(user.username)}&length=1&background=6c757d&color=ffffff&size=40`;

    
    if (user.username) {
        // Giriş yapmış kullanıcı için profil menüsü
        authDiv.innerHTML = `
            <div class="profile-dropdown">
                <div class="dropdown">
                    <img src="${profileImageUrl}" 
                         alt="Profil" 
                         class="profile-img dropdown-toggle" 
                         data-bs-toggle="dropdown" 
                         aria-expanded="false"
                         id="profileImage">
                    <ul class="dropdown-menu dropdown-menu-end">
                        <li class="user-info">
                            <div class="fw-bold">Hoşgeldin, ${user.username}</div>
                            <div class="user-email">${user.email || 'Premium Üye'}</div>
                        </li>
                        <li><hr class="dropdown-divider"></li>
                        <li>
                            <a class="dropdown-item" href="#" onclick="goToProfile()">
                                <i class="bi bi-person-circle"></i>
                                Profilim
                            </a>
                        </li>
                        <li>
                            <a class="dropdown-item" href="#" onclick="goToFavorites()">
                                <i class="bi bi-heart-fill"></i>
                                Favorilerim
                            </a>
                        </li>
                        <li>
                            <a class="dropdown-item" href="#" onclick="goToWatchlist()">
                                <i class="bi bi-bookmark-fill"></i>
                                İzleme Listem
                            </a>
                        </li>
                        <li>
                            <a class="dropdown-item" href="#" onclick="goToSettings()">
                                <i class="bi bi-gear-fill"></i>
                                Ayarlar
                            </a>
                        </li>
                        <li><hr class="dropdown-divider"></li>
                        <li>
                            <a class="dropdown-item text-danger" href="#" id="logoutBtn">
                                <i class="bi bi-box-arrow-right"></i>
                                Çıkış Yap
                            </a>
                        </li>
                    </ul>
                </div>
            </div>
        `;

        // Çıkış butonuna event listener ekle
        document.getElementById('logoutBtn').addEventListener('click', async (e) => {
            e.preventDefault();
            if (confirm('Çıkış yapmak istediğinizden emin misiniz?')) {
                try {
                    await fetch(`${API_BASE_URL}/auth/logout`, {
                        method: 'POST',
                        credentials: 'include',
                    });
                    // Çıkış sonrası navbarı güncelle
                    buildNavbar({});
					window.location.href = '/';
                } catch (error) {
                    console.error('Çıkış yapılırken hata oluştu:', error);
                    alert('Çıkış yapılırken bir hata oluştu.');
                }
            }
        });

    } else {
        // Giriş yapmamış kullanıcı için butonlar
        authDiv.innerHTML = `
            <button class="btn btn-outline-light me-2 rounded-pill" data-bs-toggle="modal" data-bs-target="#authModal" data-tab="login">Giriş Yap</button>
            <button class="btn btn-outline-light rounded-pill" data-bs-toggle="modal" data-bs-target="#authModal" data-tab="register">Üye Ol</button>
        `;
    }
}

// Profil menü fonksiyonları
function goToProfile() {
    window.location.href = '/profil';
}

function goToFavorites() {
    window.location.href = '/favoriler';
}

function goToWatchlist() {
    window.location.href = '/izleme-listesi';
}

function goToSettings() {
    window.location.href = '/ayarlar';
}

// LOGIN

document.getElementById('loginForm').addEventListener('submit', async function(e) {
    e.preventDefault();

    const email = document.getElementById('loginEmail').value;
    const password = document.getElementById('loginPassword').value;

    if (email && password) {
        try {
            const response = await fetch(`${API_BASE_URL}/auth/login`, {  
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                credentials: 'include',  
                body: JSON.stringify({ email, password })
            });

            if (response.ok) {
                const data = await response.json();
                // Giriş başarılı
                alert('Giriş başarılı! Hoşgeldiniz, ' + data.username);
                console.log('giriş başarılı');
                bootstrap.Modal.getInstance(document.getElementById('authModal')).hide();

                buildNavbar(data)
                


            } else {
                const errorText = await response.text();
                alert('Giriş başarısız: ' + errorText);
            }
        } catch (error) {
            console.error('Login hatası:', error);
            alert('Giriş sırasında hata oluştu.');
        }
    } else {
        alert('Lütfen e-posta ve şifreyi doldurun.');
    }
});

document.getElementById('registerForm').addEventListener('submit',async function(e) {
            e.preventDefault();
            
            const name = document.getElementById('registerName').value;
			const surname = document.getElementById('registerSurname').value;
			const username = document.getElementById('registerUsername').value;
            const email = document.getElementById('registerEmail').value;
            const password = document.getElementById('registerPassword').value;
            const passwordConfirm = document.getElementById('registerPasswordConfirm').value;
            
            if (password !== passwordConfirm) {
                alert('Şifreler eşleşmiyor!');
                return;
            }
            
            if (name && email && password) {
				try {
				            const response = await fetch(`${API_BASE_URL}/user/register`, {  
				                method: 'POST',
				                headers: {
				                    'Content-Type': 'application/json'
				                },
				                credentials: 'include',  
				                body: JSON.stringify({name,surname,username, email, password })
				            });

				            if (response.ok) {
				                const data = await response.json();
				                // Giriş başarılı
				                alert(data.message);
				                bootstrap.Modal.getInstance(document.getElementById('authModal')).hide();

				            } else {
				                const errorText = await response.text();
				                alert('Kayıt işlemi başarısız: ' + errorText);
				            }
				        } 
				catch (error) {
				            console.error('Kayıt hatası:', error);
				            alert('Kayıt sırasında hata oluştu.');
				 }
				 finally{
					document.getElementById('registerForm').reset();
				 }
				
                bootstrap.Modal.getInstance(document.getElementById('authModal')).hide();
				
            }
        });

        // Modal açıldığında login tab'ına odaklan
     /*
        document.getElementById('authModal').addEventListener('shown.bs.modal', function() {
            document.getElementById('registerName').focus();
        });

        */

        // Tab değiştiğinde ilgili input'a odaklan
        document.getElementById('login-tab').addEventListener('shown.bs.tab', function() {
            document.getElementById('loginEmail').focus();
        });

        document.getElementById('register-tab').addEventListener('shown.bs.tab', function() {
            document.getElementById('registerName').focus();
        });

        // Social login butonları
        document.querySelectorAll('.social-btn').forEach(btn => {
            btn.addEventListener('click', function() {
                const icon = this.querySelector('i');
                let provider = 'Social Media';
                
                if (icon.classList.contains('fa-google')) provider = 'Google';
                else if (icon.classList.contains('fa-facebook-f')) provider = 'Facebook';
                else if (icon.classList.contains('fa-twitter')) provider = 'Twitter';
                
                alert(`${provider} ile giriş yapılacak (Demo)`);
            });
        });


		document.addEventListener('DOMContentLoaded', function() {
		        var authModal = document.getElementById('authModal');
		        
		        authModal.addEventListener('show.bs.modal', function(event) {
		            var button = event.relatedTarget; // Tıklanan buton
		            var tabToOpen = button.getAttribute('data-tab'); // "login" veya "register"
		            
		            if (tabToOpen) {
		                var tabTrigger = new bootstrap.Tab(document.querySelector('#' + tabToOpen + '-tab'));
		                tabTrigger.show();
		            }
		        });
		    });		





// Film öneri butonu kodu
document.getElementById('filmOnerBtn').addEventListener('click', async () => {
  try {
    const response = await fetch(`${API_BASE_URL}/auth/status`, {
      method: 'GET',
      credentials: 'include', 
      headers: {
        'Content-Type': 'application/json'
      }
    });


     if (!response.ok) {
      console.log("Kullanıcı authenticated değil, auth modal açılıyor");
      new bootstrap.Modal(document.getElementById('authModal')).show();
      return;
    }


    const data = await response.json();
    console.log("API Yanıtı:", data); // Yanıtı konsolda kontrol edin
    
    if (data.authenticated) {
      new bootstrap.Modal(document.getElementById('filmOneriModal')).show();
    } else {
      new bootstrap.Modal(document.getElementById('authModal')).show();
    }
  } catch (err) {
    console.error('Hata:', err);
  }
});





/*
function ensureGuestId() {
        // Eğer daha önce cookie'ye yazıldıysa onu kullan
        const getCookie = (name) => {
            const match = document.cookie.match(new RegExp('(^| )' + name + '=([^;]+)'));
            return match ? match[2] : null;
        };

        let guestId = getCookie("guestId");

        // Cookie'de yoksa yeni üret
        if (!guestId) {
            guestId = crypto.randomUUID(); // Modern tarayıcılarda çalışır
            //document.cookie = "guestId=" + guestId + "; path=/; max-age=" + (60 * 60 * 24 * 365);
            document.cookie = "guestId=" + guestId + "; path=/; max-age=31536000; SameSite=Lax";

        }

        return guestId;
    }
*/	
	function ensureGuestId() {
	    // Cookie'den değer okuma fonksiyonu
	    const getCookie = (name) => {
	        const match = document.cookie.match(new RegExp('(^| )' + name + '=([^;]+)'));
	        return match ? match[2] : null;
	    };

	    let guestId = getCookie("guestId");

	    if (!guestId) {
	        if (window.crypto && crypto.randomUUID) {
	            guestId = crypto.randomUUID();
	        } else {
	            guestId = generateUUID();
	        }

	        document.cookie = "guestId=" + guestId + "; path=/; max-age=31536000; SameSite=Lax";
	    }

	    return guestId;
	}
	
	function generateUUID() {
	    return 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function(c) {
	        const r = Math.random() * 16 | 0,
	              v = c === 'x' ? r : (r & 0x3 | 0x8);
	        return v.toString(16);
	    });
	}