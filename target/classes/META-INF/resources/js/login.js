
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
      //console.log("recaptcha: "+result.recaptchaResult);
      //console.log("token: "+result.accessToken);

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

function validateLogin() {
  const myHeaders = new Headers();

  myHeaders.append(
    "Authorization",
    `Bearer: ${sessionStorage.getItem("token")}`
  );

  const requestOptions = {
    method: "GET",
    headers: myHeaders,
    redirect: "follow",
  };

  fetch(urlLogged, requestOptions)
    .then(response =>  {
      if (response.redirected) {
        window.location.href = "/pages/login.html";
      } else {
        return response.json();

      }
    })
    .then(data => {
      

        contenedor.innerHTML = "";
        const nuevoH1 = document.createElement("h1");
        nuevoH1.textContent = `Bievenido: ${data.nombre}`;
        contenedor.appendChild(nuevoH1);
    })
    .catch((error) => {
      console.error(error);
    });
}

    

