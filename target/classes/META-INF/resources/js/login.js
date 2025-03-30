
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
    .then(response => {
        if (response.ok) {
          return response.json();  // Si la respuesta es exitosa, manejamos los datos
        } else if (response.status === 401 || response.status === 403) {
          // Si el servidor nos dice que no estamos autorizados, redirigimos al login
          window.location.href = "/pages/login.html";
          return;  // Salir del flujo para evitar otros procesamientos
        } else {
          throw new Error("Algo salió mal con la respuesta del servidor");
        }
      })
      .then(result => {
        if (result) {
          const contenedor = document.getElementById("contenedor");
          contenedor.innerHTML = ""; // Limpiar el contenedor

          const nuevoH1 = document.createElement("h1");
          nuevoH1.textContent = `Bienvenido: ${result.nombre}`;
          contenedor.appendChild(nuevoH1);
        }
      })
      .catch(error => {
        console.error(error);
      });
}

    

