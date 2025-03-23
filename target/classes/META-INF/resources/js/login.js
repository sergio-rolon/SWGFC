//const recaptchaResponse = document.getElementById("g-recaptcha-response");
//const username = document.getElementById("username");
//const password = document.getElementById("password");
let url =
  window.location.hostname === "localhost"
    ? "http://localhost:8080/api/login"
    : "https://flotilla-mktpromomarc.onrender.com/api/login";

function sendDataLogin(token, email, contrasena) {
  //console.log("username "+username);
  //console.log("password "+password);
  //console.log("token "+token);
  // let email = "admin@asesorenservicios.com";
  //let contrasena = "administrador";

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
        console.log("Recaptcha successfull");
        console.log("token" + result.accessToken);
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
