let usuariosData = [];
let rowsFiltered = [];
let urlLogged = "/api/usuarios/logged";
let url = "/api/usuarios";
let actualizarButtonIsActive = false;
let currentPage = 1;
const rowsPerPage = 5;
const contenedor = document.getElementById("contenedor");
const tbody = document.getElementById("tableBody");

const emailError = document.getElementById("emailError");
const nombreError = document.getElementById("nombreError");
const apellidoPaternoError = document.getElementById("apellidoPaternoError");
const apellidoMaternoError = document.getElementById("apellidoMaternoError");
const numeroTrabajadorError = document.getElementById("numeroTrabajadorError");
const contrasenaError = document.getElementById("contrasenaError");
const idTipoUsuarioError = document.getElementById("idTipoUsuarioError");
const idTipoEstatusError = document.getElementById("idTipoEstatusError");

const idUsuario = document.getElementById("idUsuario");
const email = document.getElementById("email");
const nombre = document.getElementById("nombre");
const apellidoPaterno = document.getElementById("apellidoPaterno");
const apellidoMaterno = document.getElementById("apellidoMaterno");
const numeroTrabajador = document.getElementById("numeroTrabajador");
const contrasena = document.getElementById("contrasena");
const idTipoEstatus = document.getElementById("idTipoEstatus");
const idTipoUsuario = document.getElementById("idTipoUsuario");

// *********************Execution at start
window.addEventListener("pageshow", function (event) {
  if (event.persisted) {
    window.location.reload();
  }
});
validateLogin();
getAllUsuarios();

// ************************************** Events
document
  .getElementById("clickToLogOut")
  .addEventListener("click", function (event) {
    event.preventDefault();
    sessionStorage.removeItem("token");
    window.location.href = "/pages/login.html";
  });

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

document
  .getElementById("btnActualizar")
  .addEventListener("click", function (event) {
    event.preventDefault();
    clearErrors();
    if (actualizarButtonIsActive) {
      if (!validateNull()) {
        const raw = JSON.stringify({
          idUsuario: idUsuario.value,
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
      }
    } else {
      Swal.fire({
        title: "Operación inválida",
        text: "Elige primero un usuario para editar",
        icon: "error",
      });
    }
  });

document
  .getElementById("btnLimpiar")
  .addEventListener("click", function (event) {
    event.preventDefault();
    clearAll();
    actualizarButtonIsActive = false;
  });

//************************************** Functions

function exportToXlsx() {
  const headers = [
    "Id Usuario",
    "Email",
    "Nombre",
    "Apellido paterno",
    "Apellido materno",
    "Número trabajador",
    "Estatus",
    "Tipo usuario",
  ];

  const rows = usuariosData.map((usuario) => [
    usuario.idUsuario,
    usuario.email,
    usuario.nombre,
    usuario.apellidoPaterno,
    usuario.apellidoMaterno,
    usuario.numeroTrabajador,
    usuario.estatus,
    usuario.tipoUsuario,
  ]);

  const workSheet = XLSX.utils.aoa_to_sheet([headers, ...rows]);

  const workBook = XLSX.utils.book_new();
  XLSX.utils.book_append_sheet(workBook, workSheet, "Usuarios");

  XLSX.writeFile(workBook, "usuarios_reporte.xlsx");
}

function populateSecondDropdown() {
  const secondDropdown = document.getElementById("secondDropdown");
  secondDropdown.innerHTML = "";

  const uniqueTipoUsuario = [
    ...new Set(usuariosData.map((u) => `${u.tipoUsuario}`)),
  ];

  uniqueTipoUsuario.forEach((usuario) => {
    const label = document.createElement("label");
    label.innerHTML = `
      <input type="checkbox" class="second-filter" value="${usuario}" checked /> ${usuario}
    `;
    secondDropdown.appendChild(label);
    secondDropdown.appendChild(document.createElement("br"));
  });
}

function filterSelection() {
  const selectedEstatus = Array.from(
    document.querySelectorAll(".estatus-filter:checked")
  ).map((cb) => cb.value);
  const selectedTipoUsuario = Array.from(
    document.querySelectorAll(".second-filter:checked")
  ).map((cb) => cb.value);

  const filteredSelection = usuariosData.filter(
    (usuario) =>
      selectedEstatus.includes(usuario.estatus) &&
      selectedTipoUsuario.includes(usuario.tipoUsuario)
  );

  createTable(filteredSelection);
}

document.addEventListener("change", (event) => {
  if (
    event.target.classList.contains("estatus-filter") ||
    event.target.classList.contains("second-filter")
  ) {
    filterSelection();
  }
});

function sidebar() {
  if (flag) {
    document.getElementById("mySidebar").style.width = "0";
    document.getElementById("main").style.marginLeft = "0";
    flag = false;
  } else {
    document.getElementById("mySidebar").style.width = "200px";
    document.getElementById("main").style.marginLeft = "200px";
    flag = true;
  }
}

function clearAll() {
  clearErrors();
  clearForm();
}

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

function clearForm() {
  email.value = "";
  nombre.value = "";
  apellidoPaterno.value = "";
  apellidoMaterno.value = "";
  numeroTrabajador.value = "";
  contrasena.value = "";
  idTipoEstatus.value = "1";
  idTipoUsuario.value = "1";
  idUsuario.value = "";
  actualizarButtonIsActive = false;
}

function setErrorMsgs(result) {
  if (result["email"] && result.email != "success") {
    emailError.textContent = result.email;
  }
  if (result["Nombre"] && result.Nombre != "success") {
    nombreError.textContent = result.Nombre;
  }
  if (result["Apellidopaterno"] && result.Apellidopaterno != "success") {
    apellidoPaternoError.textContent = result.Apellidopaterno;
  }
  if (result["Apellidomaterno"] && result.Apellidomaterno != "success") {
    apellidoMaternoError.textContent = result.Apellidomaterno;
  }
  if (result["numeroTrabajador"] && result.numeroTrabajador != "success") {
    numeroTrabajadorError.textContent = result.numeroTrabajador;
  }
  if (result["contrasena"] && result.contrasena != "success") {
    contrasenaError.textContent = result.contrasena;
  }
  if (result["idTipoEstatus"] && result.idTipoEstatus != "success") {
    idTipoEstatusError.textContent = result.idTipoEstatus;
  }
  if (result["idTipoUsuario"] && result.idTipoUsuario != "success") {
    idTipoUsuarioError.textContent = result.idTipoUsuario;
  }
}

function editeUsuario(usuarioString) {
  const elementTop =
    document.getElementById("main").getBoundingClientRect().top +
    window.scrollY;
  window.scrollTo({
    top: elementTop - 46,
    behavior: "smooth",
  });
  clearAll();
  const usuario = JSON.parse(usuarioString);
  idUsuario.value = usuario.idUsuario;
  email.value = usuario.email;
  nombre.value = usuario.nombre;
  apellidoPaterno.value = usuario.apellidoPaterno;
  apellidoMaterno.value = usuario.apellidoMaterno;
  numeroTrabajador.value = usuario.numeroTrabajador;
  contrasena.value = usuario.contrasena;
  if (usuario.estatus == "Activo") {
    idTipoEstatus.value = 1;
  } else {
    idTipoEstatus.value = 2;
  }
  if (usuario.tipoUsuario == "Administrador") {
    idTipoUsuario.value = 1;
  } else if (usuario.tipoUsuario == "Operación") {
    idTipoUsuario.value = 2;
  } else {
    idTipoUsuario.value = 3;
  }

  actualizarButtonIsActive = true;
}

function createTable(usuarios, page = 1) {
  tbody.innerHTML = "";

  const startIndex = (page - 1) * rowsPerPage;
  const endIndex = startIndex + rowsPerPage;
  const paginatedUsuarios = usuarios.slice(startIndex, endIndex);

  paginatedUsuarios.forEach((usuario) => {
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
          <td><button class="edit-btn" onclick="editeUsuario('${usuarioString}')">Editar</button></td>
          <td><button class="delete-btn" onclick="deleteUsuario('${usuario.email}')">Eliminar</button></td>
          `;

    tbody.appendChild(row);
  });

  renderPagination(usuarios, page);
}

function renderPagination(usuarios, page) {
  const paginationContainer = document.getElementById("paginationDiv");
  paginationContainer.innerHTML = "";

  const pageCount = Math.ceil(usuarios.length / rowsPerPage);

  if (page > 1) {
    const prevButton = document.createElement("button");
    prevButton.textContent = "Anterior";
    prevButton.addEventListener("click", () => {
      createTable(usuarios, page - 1);
    });
    paginationContainer.appendChild(prevButton);
  }

  for (let i = 1; i <= pageCount; i++) {
    const pageButton = document.createElement("button");
    pageButton.textContent = i;
    if (i === page) {
      pageButton.classList.add("active");
    }
    pageButton.addEventListener("click", () => {
      createTable(usuarios, i);
    });
    paginationContainer.appendChild(pageButton);
  }

  if (page < pageCount) {
    const nextButton = document.createElement("button");
    nextButton.textContent = "Siguiente";
    nextButton.addEventListener("click", () => {
      createTable(usuarios, page + 1);
    });
    paginationContainer.appendChild(nextButton);
  }
}
function showActiveUsers() {
  const activos = usuariosData.filter((u) => u.estatus === "Activo");
  createTable(activos);
}

function showInactiveUsers() {
  const noActivos = usuariosData.filter((u) => u.estatus === "Inactivo");
  createTable(noActivos);
}

function showAllUsers() {
  createTable(usuariosData);
  populateSecondDropdown();
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
        return response.json();
      } else if (response.status === 401) {
        sessionStorage.removeItem("token");
        window.location.href = "/pages/login.html";
      } else if (response.status === 403) {
        window.location.href = "/index.html";
      } else {
        throw new Error("Algo salió mal con la respuesta del servidor");
      }
    })
    .then((usuario) => {
      if (usuario) {
        if (usuario.role === "Administrador") {
          document.getElementById("emailUserLogged").textContent =
            usuario.email;
          document.getElementById("loader").style.display = "none";
          document.getElementById("contenido").style.visibility = "visible";
        } else {
          window.location.href = "/index.html";
        }
      }
    })
    .catch((error) => {
      let errorMsg = error;
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
        return response.json();
      } else if (response.status === 403) {
        window.location.href = "/index.html";
        return;
      } else if (response.status === 401) {
        sessionStorage.removeItem("token");
        window.location.href = "/pages/login.html";
      } else {
        throw new Error("Algo salió mal con la respuesta del servidor");
      }
    })
    .then((result) => {
      if (result) {
        usuariosData = result.myArrayList.map((item) => item.map);
        rowsFiltered = [...usuariosData];
        createTable(usuariosData);
        populateSecondDropdown();
      }
    })
    .catch((error) => {
      let errorMsg = error;
    });
}

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
          Swal.fire({
            title: "Operación exitosa",
            text: result.success,
            icon: "success",
          });
          return result;
        } else if (response.status === 400) {
          setErrorMsgs(result);
          throw new Error("Error");
        } else if (response.status === 401) {
          sessionStorage.removeItem("token");
          window.location.href = "/pages/login.html";
        } else if (response.status === 409) {
          Swal.fire({
            title: "Operación fallida",
            text: result.error,
            icon: "error",
          });
          throw new Error("Error");
        } else {
          throw new Error("Algo salió mal con la respuesta del servidor");
        }
      });
    })
    .then((result) => {
      if (result) {
        getAllUsuarios();
        clearAll();
      }
    })
    .catch((error) => {
      let errorMsg = error;
    });
}

function deleteUsuario(email) {
  Swal.fire({
    title: "¿Quieres eliminar este usuario?",
    text: "Esta acción no podrá revertirse.",
    icon: "warning",
    showCancelButton: true,
    confirmButtonColor: "#3085d6",
    cancelButtonColor: "#d33",
    confirmButtonText: "Eliminar definitivamente",
    cancelButtonText: `Cancelar`,
  }).then((result) => {
    if (result.isConfirmed) {
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
            return response.json();
          } else if (response.status === 403) {
            window.location.href = "/index.html";
          } else if (response.status === 401) {
            sessionStorage.removeItem("token");
            window.location.href = "/pages/login.html";
          } else {
            throw new Error("Algo salió mal con la respuesta del servidor");
          }
        })
        .then((result) => {
          if (result) {
            Swal.fire({
              title: "Operación exitosa",
              text: result.success,
              icon: "success",
            });
            getAllUsuarios();
          }
        })
        .catch((error) => {
          let errorMsg = error;
        });
    }
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
    .then((response) => {
      return response.json().then((result) => {
        if (response.ok) {
          Swal.fire({
            title: "Operación exitosa",
            text: result.success,
            icon: "success",
          });
          return result;
        } else if (response.status === 400) {
          setErrorMsgs(result);
          throw new Error("Error");
        } else if (response.status === 401) {
          sessionStorage.removeItem("token");
          window.location.href = "/pages/login.html";
        } else if (response.status === 409) {
          Swal.fire({
            title: "Operación fallida",
            text: result.error,
            icon: "error",
          });
          throw new Error("Error");
        }
      });
    })
    .then((result) => {
      if (result) {
        getAllUsuarios();
        clearAll();
        actualizarButtonIsActive = false;
      }
    })
    .catch((error) => {
      let errorMsg = error;
    });
}
const searchInput = document.getElementById("searchInput");
const tableBody = document.getElementById("tableBody");

searchInput.addEventListener("input", function () {
  const searchedValue = this.value.toLowerCase().trim();

  rowsFiltered = usuariosData.filter((usuario) =>
    usuario.numeroSerie.toLowerCase().includes(searchedValue)
  );

  createTable(rowsFiltered, 1);
});
