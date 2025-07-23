document.addEventListener("DOMContentLoaded", function () {
  loadUserInfo(); 
});

function loadUserInfo() {
  fetch(`${API_BASE_URL}/user/userinfo`, {
    method: "GET",
    credentials: "include" 
  })
  .then(res => {
    if (!res.ok) {
      throw new Error('Sunucu hatası: ' + res.status);
    }
    return res.json();  
  })
  .then(data => {
    console.log(data);
	document.getElementById('profile-image').src = `https://ui-avatars.com/api/?name=${data.name}&length=1&background=6c757d&color=ffffff&size=40`
	document.getElementById('under-profile-nameSurname').textContent = data.name + " " + data.surname;
    document.getElementById('name').value = data.name; 
    document.getElementById('surname').value = data.surname;
    document.getElementById('username').value = data.userName;
    document.getElementById('email').value = data.email;
  })
  .catch(err => console.error(err));
}


document.getElementById('btnEdit').addEventListener("click", () =>
	{
		document.getElementById('name').disabled=false;
		document.getElementById('surname').disabled=false;
		
		document.getElementById('btnEdit').classList.add('hidden');
		document.getElementById('btnSave').classList.remove('hidden');
		
	}
)
/*
function updateUser()
{
	document.getElementById('btnSave').addEventListener("click", () =>
	{
		const name = document.getElementById('name').value;
		const surname = document.getElementById('surname').value;
		
		fetch(`${API_BASE_URL}/user/update`,{
				method:"POST",
				credentials:"include",
				headers: {
				            "Content-Type": "application/json"
				        },
				body: JSON.stringify({
				            name: name,
				            surname: surname
				        })		
		})
		.then(response => {
			if(!response.ok)
				{
					throw new Error('Sunucu hatası : '+ response.status);
				}
				return response.json();
		})
		.then(data =>{
			document.getElementById('name').disabled = true;
			document.getElementById('surname').disabled = true;
			document.getElementById('under-profile-nameSurname').textContent = data.name + " "+ data.surName;
			

			document.getElementById('btnEdit').classList.remove('hidden');
			document.getElementById('btnSave').classList.add('hidden');
		})
		.catch(error => {
			console.error("Güncelleme hatası:", error);
			alert("Güncelleme sırasında bir hata oluştu. Lütfen daha sonra tekrar deneyin.");
		});
		
			
	
		document.getElementById('name').disabled=true;
		document.getElementById('surname').disabled=true;
		
		document.getElementById('btnSave').classList.add('hidden');
		document.getElementById('btnEdit').classList.remove('hidden');
		

});

}
*/

function updateUser() {
  const name = document.getElementById('name').value;
  const surname = document.getElementById('surname').value;

  fetch(`${API_BASE_URL}/user/update`, {
    method: "POST",
    credentials: "include",
    headers: {
      "Content-Type": "application/json"
    },
    body: JSON.stringify({
      name: name,
      surname: surname
    })
  })
    .then(response => {
      if (!response.ok) {
        throw new Error('Sunucu hatası : ' + response.status);
      }
      return response.json();
    })
    .then(data => {
      document.getElementById('name').disabled = true;
      document.getElementById('surname').disabled = true;
	  document.getElementById('name').value=data.name;
	  document.getElementById('surname').value=data.surName;
	  document.getElementById('profile-image').src= `https://ui-avatars.com/api/?name=${data.name}&length=1&background=6c757d&color=ffffff&size=40`
      document.getElementById('under-profile-nameSurname').textContent = data.name + " " + data.surName;

      document.getElementById('btnEdit').classList.remove('hidden');
      document.getElementById('btnSave').classList.add('hidden');
    })
    .catch(error => {
      console.error("Güncelleme hatası:", error);
      alert("Güncelleme sırasında bir hata oluştu. Lütfen daha sonra tekrar deneyin.");
    });
}



document.getElementById("profile-form").addEventListener("submit", function (e) {
  e.preventDefault();
  
  const form = e.target;
    if (!form.checkValidity()) {
      form.reportValidity();
      return;
    }
	
	
	updateUser();
});

/*
document.getElementById("password-form").addEventListener("submit", function (e) {
  e.preventDefault();
  
  const form = e.target;
    if (!form.checkValidity()) {
      form.reportValidity();
      return;
    }
	
	
	// fetch
});
*/


document.getElementById('password-form').addEventListener('submit', function (e) {
  e.preventDefault();
  


  // Inputları al
  const currentPasswordEl = document.getElementById('currentPassword');
  const newPasswordEl = document.getElementById('newPassword');
  const newPasswordConfirmEl = document.getElementById('newPasswordConfirm');

  // Değerleri al
  const currentPassword = currentPasswordEl.value.trim();
  const newPassword = newPasswordEl.value.trim();
  const newPasswordConfirm = newPasswordConfirmEl.value.trim();

  // Hata mesaj divleri
  const currentError = document.getElementById('currentPasswordError');
  const newError = document.getElementById('newPasswordError');
  const confirmError = document.getElementById('newPasswordConfirmError');

  // Önce tüm hata mesajlarını temizle
  [currentPasswordEl, newPasswordEl, newPasswordConfirmEl].forEach(input => {
    input.classList.remove('is-invalid');
  });
  [currentError, newError, confirmError].forEach(div => div.textContent = '');

  const passwordPattern =   /^(?=.*\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[@$!%*?&_])[A-Za-z\d@$!%*?&_]{8,}$/;
  let hasError = false;

  if (!currentPassword) {
    currentPasswordEl.classList.add('is-invalid');
    currentError.textContent = 'Mevcut şifre boş olamaz.';
    hasError = true;
  }

  if (!passwordPattern.test(newPassword)) {
    newPasswordEl.classList.add('is-invalid');
    newError.textContent = 'Şifre en az 8 karakter, büyük harf, küçük harf, rakam ve özel karakter içermeli.';
    hasError = true;
  }

  if (newPassword !== newPasswordConfirm) {
    newPasswordConfirmEl.classList.add('is-invalid');
    confirmError.textContent = 'Yeni şifreler eşleşmiyor.';
    hasError = true;
  }

  if (currentPassword === newPassword && currentPassword !== '') {
    newPasswordEl.classList.add('is-invalid');
    newError.textContent = 'Yeni şifre mevcut şifreyle aynı olamaz.';
    hasError = true;
  }

  if (!hasError) {
    // Şifre değiştir işlemi
    changePassword(currentPassword, newPassword);
  }
});

  async function changePassword(currentPassword, newPassword) {
    try {
      const res = await fetch(`${API_BASE_URL}/user/change-password`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        credentials: 'include',
        body: JSON.stringify({ currentPassword, newPassword })
      });

      if (res.ok) {
        alert("Şifre başarıyla değiştirildi.");
        // Formu sıfırla
        document.getElementById('password-form').reset();
      } else {
        const data = await res.json();
        alert(data.message || "Şifre değiştirilemedi.");
      }
    } catch (err) {
      console.error("Hata:", err);
      alert("Bir hata oluştu, lütfen tekrar deneyin.");
    }
  }




