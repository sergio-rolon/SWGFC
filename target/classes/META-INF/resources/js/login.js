let url =
  window.location.hostname === "localhost"
    ? "http://localhost:8080/api/login"
    : "https://flotilla-mktpromomarc.onrender.com/api/login";

let urlLogged =
  window.location.hostname === "localhost"
    ? "http://localhost:8080/api/usuarios/logged"
    : "https://flotilla-mktpromomarc.onrender.com/api/usuarios/logged";

const contenedor = document.getElementById("contenedor");

function sendDataLogin(token, email, contrasena) {
  const myHeaders = new Headers();
  myHeaders.append("Content-Type", "application/json");

  const raw = JSON.stringify({
    token: token,
    email: email,
    contrasena: contrasena,
  });

  const requestOptions = {
    method: "POST",
    headers: myHeaders,
    body: raw,
    redirect: "follow",
  };

  fetch(url, requestOptions)
    .then((response) => response.json())
    .then((result) => {
      if (result.recaptchaResult == "true" && result.accessToken != "false") {
        sessionStorage.setItem("token", result.accessToken);
        window.location.href = "/index.html";
      } else if (
        result.recaptchaResult == "true" &&
        result.accessToken == "false"
      ) {
        console.log("Recaptcha successfull");
        console.log("Usuario o contraseña invalidos");
      } else {
        console.log("Error en captcha");
      }
    })
    .catch((error) => {
      console.error(error);
    });
}
