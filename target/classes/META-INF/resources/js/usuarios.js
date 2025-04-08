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
    window.location.href = "/index.html";
  });

const tbody = document.getElementById('tableBody');
validateLogin();
getAllUsuarios();

  document
  .getElementById("btnRegistrar")
  .addEventListener("click", function (event) {
    event.preventDefault();
    const raw = JSON.stringify({
      email: email.value,
      nombre: nombre.value,
      apellidoPaterno: apellidoPaterno.value,
      apellidoMaterno: apellidoMaterno.value,
      numeroTrabajador: numeroTrabajador.value,
      contrasena: contrasena.value,
      idTipoEstatus: idTipoEstatus.value,
      idTipoUsuario: idTipoUsuario.value
    });

    registerUsuario(raw);
  });



document
  .getElementById("btnActualizar").addEventListener("click", function (event) {
    event.preventDefault();

    const raw = JSON.stringify({
      email: email.value,
      nombre: nombre.value,
      apellidoPaterno: apellidoPaterno.value,
      apellidoMaterno: apellidoMaterno.value,
      numeroTrabajador: numeroTrabajador.value,
      contrasena: contrasena.value,
      idTipoEstatus: idTipoEstatus.value,
      idTipoUsuario: idTipoUsuario.value
    });

    
    updateUsuario(raw);
  });

  
document
  .getElementById("btnLimpiar")
  .addEventListener("click", function (event) {
    event.preventDefault();
    cleanForm();
  });

function cleanForm() {
    email.value="";
    nombre.value="";
    apellidoPaterno.value="";
    apellidoMaterno.value="";
    numeroTrabajador.value="";
    contrasena.value="";
    idTipoEstatus.value="";
    idTipoUsuario.value="";
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
    .then(response => {
        if (response.ok) {
          return response.json();  // Si la respuesta es exitosa, manejamos los datos
        } else if (response.status === 401 || response.status === 403) {
          // Si el servidor nos dice que no estamos autorizados, redirigimos al login
          window.location.href = "/index.html";
          return;  // Salir del flujo para evitar otros procesamientos
        } else {
          throw new Error("Algo salió mal con la respuesta del servidor");
        }
    })
    .then(result => {
      if (result) {
        tbody.innerHTML = '';
        result.myArrayList.forEach((item, index) => {
          // Acceder a los valores dentro de "map"
          const mapData = item.map;

          const row = document.createElement("tr");

          row.innerHTML = `
          <td>${mapData.idUsuario}</td>
          <td>${mapData.email}</td>
          <td>${mapData.nombre}</td>
          <td>${mapData.apellidoPaterno}</td>
          <td>${mapData.apellidoMaterno}</td>
          <td>${mapData.numeroTrabajador}</td>
          <td>${mapData.contrasena}</td>
          <td>${mapData.estatus}</td>
          <td>${mapData.tipoUsuario}</td>
          <td>
          <button onclick="editeUsuario(${JSON.stringify(mapData)})">Editar</button>
          <button onclick="deleteUsuario('${mapData.email}')">Eliminar</button>
          </td>
          `;
          
          tbody.appendChild(row);
        
        });
      }
    })
    .catch((error) => {
      console.error(error);
    });
}

function deleteUsuario(email) {
  const raw = JSON.stringify({email: email});


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
    .then(response => {
        if (response.ok) {
          return response.json();  // Si la respuesta es exitosa, manejamos los datos
        } else if (response.status === 401 || response.status === 403) {
          // Si el servidor nos dice que no estamos autorizados, redirigimos al login
          window.location.href = "/index.html";
          return;  // Salir del flujo para evitar otros procesamientos
        } else {
          throw new Error("Algo salió mal con la respuesta del servidor");
        }
    })
    .then(result => {
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
const idTipoEstatus = document.getElementById('idTipoEstatus');
const idTipoUsuario = document.getElementById('idTipoUsuario');


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
    .then(response => {
        if (response.ok) {
          return response.json();  // Si la respuesta es exitosa, manejamos los datos
        } else if (response.status === 401 || response.status === 403) {
          // Si el servidor nos dice que no estamos autorizados, redirigimos al login
          window.location.href = "/index.html";
          return;  // Salir del flujo para evitar otros procesamientos
        } else {
          throw new Error("Algo salió mal con la respuesta del servidor");
        }
    })
    .then(result => {
      if (result) {
        getAllUsuarios();
      }
    })
    .catch((error) => {
      console.error(error);
    });

  
}

function updateUsuario(raw) {
  
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
    .then(response => {
        if (response.ok) {
          return response.json();  // Si la respuesta es exitosa, manejamos los datos
        } else if (response.status === 401 || response.status === 403) {
          // Si el servidor nos dice que no estamos autorizados, redirigimos al login
          window.location.href = "/index.html";
          return;  // Salir del flujo para evitar otros procesamientos
        } else {
          throw new Error("Algo salió mal con la respuesta del servidor");
        }
    })
    .then(result => {
      if (result) {
        getAllUsuarios();
      }
    })
    .catch((error) => {
      console.error(error);
    });

  
}

function editeUsuario(usuarioString) {

  const usuario = JSON.parse(usuarioString);
    email.value=usuario.email;
    nombre.value=usuario.nombre;
    apellidoPaterno.value=usuario.apellidoPaterno;
    apellidoMaterno.value=usuario.apellidoMaterno;
    numeroTrabajador.value=usuario.numeroTrabajador;
    contrasena.value = usuario.contrasena;
  if (usuario.estatus=="activo") {
    idTipoEstatus.value=1;
  }
  idTipoEstatus.value=2;
    if (usuario.tipoUsuario=="administrador") {
      idTipoUsuario.value = 1;
    } else if (usuario.tipoUsuario=="operacion") {
      idTipoUsuario.value = 2;
  }
      idTipoUsuario.value = 3;
}


