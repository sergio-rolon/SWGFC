let usuariosData = [];

let urlLogged =
  window.location.hostname === "localhost"
    ? "http://localhost:8080/api/usuarios/logged"
    : "https://flotilla-mktpromomarc.onrender.com/api/usuarios/logged";

let url =
  window.location.hostname === "localhost"
    ? "http://localhost:8080/api/usuarios"
    : "https://flotilla-mktpromomarc.onrender.com/api/usuarios";

const contenedor = document.getElementById("contenedor");

document
  .getElementById("clickToLogOut")
  .addEventListener("click", function (event) {
    event.preventDefault();
    sessionStorage.removeItem("token");
    window.location.href = "/pages/login.html";
  });

const tbody = document.getElementById("tableBody");
validateLogin();
getAllUsuarios();

document
  .getElementById("btnRegistrar")
  .addEventListener("click", function (event) {
    event.preventDefault();
    clearErrors();
    if (!validateNull()) {
      const raw = JSON.stringify({
        email: email.value,
        nombre: nombre.value,
        apellidoPaterno: apellidoPaterno.value,
        apellidoMaterno: apellidoMaterno.value,
        numeroTrabajador: numeroTrabajador.value,
        contrasena: contrasena.value,
        idTipoEstatus: idTipoEstatus.value,
        idTipoUsuario: idTipoUsuario.value,
      });

      registerUsuario(raw);
    }
  });

const emailError = document.getElementById("emailError");
const nombreError = document.getElementById("nombreError");
const apellidoPaternoError = document.getElementById("apellidoPaternoError");
const apellidoMaternoError = document.getElementById("apellidoMaternoError");
const numeroTrabajadorError = document.getElementById("numeroTrabajadorError");
const contrasenaError = document.getElementById("contrasenaError");
const idTipoUsuarioError = document.getElementById("idTipoUsuarioError");
const idTipoEstatusError = document.getElementById("idTipoEstatusError");

function clearErrors() {
  emailError.textContent = "";
  email.classList.remove("borde-rojo");

  nombreError.textContent = "";
  nombre.classList.remove("borde-rojo");

  apellidoPaternoError.textContent = "";
  apellidoPaterno.classList.remove("borde-rojo");

  apellidoMaternoError.textContent = "";
  apellidoMaterno.classList.remove("borde-rojo");

  numeroTrabajadorError.textContent = "";
  numeroTrabajador.classList.remove("borde-rojo");

  contrasenaError.textContent = "";
  contrasena.classList.remove("borde-rojo");

  idTipoUsuarioError.textContent = "";
  idTipoUsuario.classList.remove("borde-rojo");

  idTipoEstatusError.textContent = "";
  idTipoEstatus.classList.remove("borde-rojo");
}

function validateNull() {
  let flag = false;
  if (!email.value || email.value.trim() === "") {
    emailError.textContent = "Email no puede ser nulo";
    email.classList.add("borde-rojo");
    flag = true;
  }
  if (!nombre.value || nombre.value.trim() === "") {
    nombreError.textContent = "Nombre no puede ser nulo";
    nombre.classList.add("borde-rojo");
    flag = true;
  }
  if (!apellidoPaterno.value || apellidoPaterno.value.trim() === "") {
    apellidoPaternoError.textContent = "Apellido paterno no puede ser nulo";
    apellidoPaterno.classList.add("borde-rojo");
    flag = true;
  }
  if (!apellidoMaterno.value || apellidoMaterno.value.trim() === "") {
    apellidoMaternoError.textContent = "Apellido materno  no puede ser nulo";
    apellidoMaterno.classList.add("borde-rojo");
    flag = true;
  }
  if (!numeroTrabajador.value || numeroTrabajador.value.trim() === "") {
    numeroTrabajadorError.textContent = "Número trabajador no puede ser nulo";
    numeroTrabajador.classList.add("borde-rojo");
    flag = true;
  }
  if (!contrasena.value || contrasena.value.trim() === "") {
    contrasenaError.textContent = "Contraseña no puede ser nulo";
    contrasena.classList.add("borde-rojo");
    flag = true;
  }

  return flag;
}

document
  .getElementById("btnActualizar")
  .addEventListener("click", function (event) {
    event.preventDefault();
    console.log("boton actualizar clickeado");
    const raw = JSON.stringify({
      email: email.value,
      nombre: nombre.value,
      apellidoPaterno: apellidoPaterno.value,
      apellidoMaterno: apellidoMaterno.value,
      numeroTrabajador: numeroTrabajador.value,
      contrasena: contrasena.value,
      idTipoEstatus: idTipoEstatus.value,
      idTipoUsuario: idTipoUsuario.value,
    });

    updateUsuario(raw);
  });

document
  .getElementById("btnLimpiar")
  .addEventListener("click", function (event) {
    event.preventDefault();
    cleanForm();
    clearErrors();
  });

let actualizarButtonIsActive = false;

//******** functions

function cleanForm() {
  email.value = "";
  nombre.value = "";
  apellidoPaterno.value = "";
  apellidoMaterno.value = "";
  numeroTrabajador.value = "";
  contrasena.value = "";
  idTipoEstatus.value = "";
  idTipoUsuario.value = "";
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

function getAllUsuarios() {
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

  fetch(url, requestOptions)
    .then((response) => {
      if (response.ok) {
        return response.json(); // Si la respuesta es exitosa, manejamos los datos
      } else if (response.status === 401 || response.status === 403) {
        // Si el servidor nos dice que no estamos autorizados, redirigimos al login
        window.location.href = "/index.html";
        return; // Salir del flujo para evitar otros procesamientos
      } else {
        throw new Error("Algo salió mal con la respuesta del servidor");
      }
    })
    .then((result) => {
      if (result) {
        usuariosData = result.myArrayList.map((item) => item.map);
        createTable(usuariosData);
      }
    })
    .catch((error) => {
      console.error(error);
    });
}

function deleteUsuario(email) {
  const raw = JSON.stringify({ email: email });

  const myHeaders = new Headers();

  myHeaders.append("Content-Type", "application/json");
  myHeaders.append(
    "Authorization",
    `Bearer: ${sessionStorage.getItem("token")}`
  );

  const requestOptions = {
    method: "DELETE",
    headers: myHeaders,
    body: raw,
    redirect: "follow",
  };

  fetch(url, requestOptions)
    .then((response) => {
      if (response.ok) {
        return response.json(); // Si la respuesta es exitosa, manejamos los datos
      } else if (response.status === 401 || response.status === 403) {
        // Si el servidor nos dice que no estamos autorizados, redirigimos al login
        window.location.href = "/index.html";
        return; // Salir del flujo para evitar otros procesamientos
      } else {
        throw new Error("Algo salió mal con la respuesta del servidor");
      }
    })
    .then((result) => {
      if (result) {
        getAllUsuarios();
      }
    })
    .catch((error) => {
      console.error(error);
    });
}

const email = document.getElementById("email");
const nombre = document.getElementById("nombre");
const apellidoPaterno = document.getElementById("apellidoPaterno");
const apellidoMaterno = document.getElementById("apellidoMaterno");
const numeroTrabajador = document.getElementById("numeroTrabajador");
const contrasena = document.getElementById("contrasena");
const idTipoEstatus = document.getElementById("idTipoEstatus");
const idTipoUsuario = document.getElementById("idTipoUsuario");

function registerUsuario(raw) {
  const myHeaders = new Headers();

  myHeaders.append("Content-Type", "application/json");
  myHeaders.append(
    "Authorization",
    `Bearer: ${sessionStorage.getItem("token")}`
  );

  const requestOptions = {
    method: "POST",
    headers: myHeaders,
    body: raw,
    redirect: "follow",
  };

  fetch(url, requestOptions)
    .then((response) => {
      return response.json().then((result) => {
        if (response.ok) {
          return result; // Si la respuesta es exitosa, manejamos los datos
        } else if (response.status === 400) {
          setErrorMsgs(result);
          throw new Error("Error");
        } else {
          throw new Error("Algo salió mal con la respuesta del servidor");
        }
      });
    })
    .then((result) => {
      if (result) {
        getAllUsuarios();
      }
    })
    .catch((error) => {
      let errorMsg = error;
    });
}

function setErrorMsgs(result) {
  if (result["email"]) {
    emailError.textContent = result.email;
  }
  if (result["Nombre"]) {
    nombreError.textContent = result.Nombre;
  }
  if (result["Apellidopaterno"]) {
    apellidoPaternoError.textContent = result.Apellidopaterno;
  }
  if (result["Apellidomaterno"]) {
    apellidoMaternoError.textContent = result.Apellidomaterno;
  }
  if (result["numeroTrabajador"]) {
    numeroTrabajadorError.textContent = result.numeroTrabajador;
  }
  if (result["contrasena"]) {
    contrasenaError.textContent = result.contrasena;
  }
  if (result["idTipoEstatus"]) {
    idTipoEstatusError.textContent = result.idTipoEstatus;
  }
  if (result["idTipoUsuario"]) {
    idTipoUsuarioError.textContent = result.idTipoUsuario;
  }
}

function updateUsuario(raw) {
  if (actualizarButtonIsActive) {
    const myHeaders = new Headers();

    myHeaders.append("Content-Type", "application/json");
    myHeaders.append(
      "Authorization",
      `Bearer: ${sessionStorage.getItem("token")}`
    );

    const requestOptions = {
      method: "PUT",
      headers: myHeaders,
      body: raw,
      redirect: "follow",
    };

    fetch(url, requestOptions)
      .then((response) => {
        if (response.ok) {
          return response.json(); // Si la respuesta es exitosa, manejamos los datos
        } else if (response.status === 401 || response.status === 403) {
          // Si el servidor nos dice que no estamos autorizados, redirigimos al login
          window.location.href = "/index.html";
          return; // Salir del flujo para evitar otros procesamientos
        } else {
          throw new Error("Algo salió mal con la respuesta del servidor");
        }
      })
      .then((result) => {
        if (result) {
          getAllUsuarios();
        }
      })
      .catch((error) => {
        console.error(error);
      });

    actualizarButtonIsActive = false;
    console.log("boton actualizar no activo, edita un usuario primero");
  }
  //show error message is not active
  //TODO
}

function editeUsuario(usuarioString) {
  const usuario = JSON.parse(usuarioString);
  email.value = usuario.email;
  nombre.value = usuario.nombre;
  apellidoPaterno.value = usuario.apellidoPaterno;
  apellidoMaterno.value = usuario.apellidoMaterno;
  numeroTrabajador.value = usuario.numeroTrabajador;
  contrasena.value = usuario.contrasena;
  if (usuario.estatus == "activo") {
    idTipoEstatus.value = 1;
  } else {
    idTipoEstatus.value = 2;
  }
  if (usuario.tipoUsuario == "administrador") {
    idTipoUsuario.value = 1;
  } else if (usuario.tipoUsuario == "operacion") {
    idTipoUsuario.value = 2;
  } else {
    idTipoUsuario.value = 3;
  }

  actualizarButtonIsActive = true;
}

function createTable(usuarios) {
  tbody.innerHTML = "";
  usuarios.forEach((usuario) => {
    // Acceder a los valores dentro de "map"

    const row = document.createElement("tr");
    const usuarioString = JSON.stringify(usuario).replace(/"/g, "&quot;");
    row.innerHTML = `
          <td>${usuario.idUsuario}</td>
          <td>${usuario.email}</td>
          <td>${usuario.nombre}</td>
          <td>${usuario.apellidoPaterno}</td>
          <td>${usuario.apellidoMaterno}</td>
          <td>${usuario.numeroTrabajador}</td>
          <td>${usuario.contrasena}</td>
          <td>${usuario.estatus}</td>
          <td>${usuario.tipoUsuario}</td>
          <td><button onclick="editeUsuario('${usuarioString}')">Editar</button></td>
          <td><button onclick="deleteUsuario('${usuario.email}')">Eliminar</button></td>
          `;

    tbody.appendChild(row);
  });
}

function showActiveUsers() {
  const activos = usuariosData.filter((u) => u.estatus === "activo");
  createTable(activos);
}

function showInactiveUsers() {
  const noActivos = usuariosData.filter((u) => u.estatus === "inactivo");
  createTable(noActivos);
}

function showAllUsers() {
  createTable(usuariosData);
}
