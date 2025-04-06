let urlLogged =
  window.location.hostname === "localhost"
    ? "http://localhost:8080/api/usuarios/logged"
    : "https://flotilla-mktpromomarc.onrender.com/api/usuarios/logged";

const contenedor = document.getElementById("contenedor");

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
    .then((response) => {
      if (response.ok) {
        return response.json(); // Si la respuesta es exitosa, manejamos los datos
      } else if (response.status === 401 || response.status === 403) {
        // Si el servidor nos dice que no estamos autorizados, redirigimos al login
        window.location.href = "/pages/login.html";
        return; // Salir del flujo para evitar otros procesamientos
      } else {
        throw new Error("Algo salió mal con la respuesta del servidor");
      }
    })
    .then((usuario) => {
      if (usuario) {
        //const mainContenedor = document.getElementById("mainContenedor");
        document.getElementById("emailUserLogged").textContent = usuario.email;
        // Eliminar contenido existente
        //mainContenedor.innerHTML = "";
        document.getElementById("loader").style.display = "none"; // Oculta el loader
        document.getElementById("contenido").style.visibility = "visible";
      }
    })
    .catch((error) => {
      console.error(error);
    });
}

document
  .getElementById("clickToLogOut")
  .addEventListener("click", function (event) {
    event.preventDefault();
    sessionStorage.removeItem("token");
    window.location.href = "/index.html";
  });

function miFuncion() {
  alert("¡Imagen clickeada!");
}
