//const recaptchaResponse = document.getElementById("g-recaptcha-response");
//const username = document.getElementById("username");
//const password = document.getElementById("password");

function sendDataLogin(token) {

//console.log("username "+username);
//console.log("password "+password);
//console.log("token "+token);


const myHeaders = new Headers();
myHeaders.append("Content-Type", "application/json");

const raw = JSON.stringify({
  "token": token,
  "username": "Admin",
  "password": "Prueba12"
});

const requestOptions = {
  method: "POST",
  headers: myHeaders,
  body: raw,
  redirect: "follow"
};

fetch("https://holaworldsagaindeployed.onrender.com/api/Login", requestOptions)
  .then((response) => response.json())
  .then((result) => {
  //console.log("recaptcha: "+result.recaptchaResult);
  //console.log("token: "+result.accessToken);

  if(result.recaptchaResult=="true" && result.accessToken != "false"){
  sessionStorage.setItem("token", result.accessToken);
  console.log("Recaptcha successfull");
  console.log("token"+result.accessToken);
  }else if(result.recaptchaResult=="true" && result.accessToken == "false"){
  console.log("Recaptcha successfull");
  console.log("Usuario o contraseña invalidos");
  } else {
        console.log("Error en captcha")
   }}
   )
  .catch((error) => {
    console.error(error);

  });

}
