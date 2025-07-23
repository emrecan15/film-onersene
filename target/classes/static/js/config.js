if (typeof API_BASE_URL === 'undefined') {
  var API_BASE_URL = window.location.hostname === 'localhost' || window.location.hostname.startsWith('192.168.')
    ? "http://192.168.1.80:8080/api"
    : "https://film-onersene.onrender.com/api";
}