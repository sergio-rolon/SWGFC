let incidentesData = [];
let clientesData = [];
let asignacionesData = [];
let urlLogged = "/api/usuarios/logged";
let url = "/api/incidentes";
let urlAsignaciones = "/api/asignaciones/getAsignacionesParaIncidentes";
let urlClientes = "/api/clientes/getAllClientes";
let actualizarButtonIsActive = false;
let currentPage = 1;
const rowsPerPage = 5;
const contenedor = document.getElementById("contenedor");
const tbody = document.getElementById("tableBody");

const idTipoIncidenteError = document.getElementById("idTipoIncidenteError");
const descripcionError = document.getElementById("descripcionError");
const fechaIncidenteError = document.getElementById("fechaIncidenteError");
const idAsignacionError = document.getElementById("idAsignacionError");

const idIncidente = document.getElementById("idIncidente");
const idTipoIncidente = document.getElementById("idTipoIncidente");
const fechaIncidente = document.getElementById("fechaIncidente");
const descripcion = document.getElementById("descripcion");

const idEmpleadoSelect = document.getElementById("idEmpleadoSelect");
const idAsignacionSelect = document.getElementById("idAsignacionSelect");
// *********************Execution at start
window.addEventListener("pageshow", function (event) {
  if (event.persisted) {
    window.location.reload();
  }
});

validateLogin();
getAllIncidentes();
getAllClientes();
getAllAsignaciones();
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
        idTipoIncidente: idTipoIncidente.value,
        descripcion: descripcion.value,
        fechaIncidente: getFormattedDate("fechaIncidente"),
        idAsignacion: idAsignacionSelect.value,
      });

      registerIncidente(raw);
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
          idIncidente: idIncidente.value,
          idTipoIncidente: idTipoIncidente.value,
          descripcion: descripcion.value,
          fechaIncidente: getFormattedDate("fechaIncidente"),
          idAsignacion: idAsignacionSelect.value,
        });
        updateIncidente(raw);
      }
    } else {
      Swal.fire({
        title: "Operación inválida",
        text: "Elige primero un incidente para editar",
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

function formatDateForCalendar(fechaObj) {
  const fecha = fechaObj.toString();
  const year = fecha.substring(0, 4);
  const month = fecha.substring(4, 6);
  const day = fecha.substring(6, 8);
  return `${year}-${month}-${day}`;
}
function formatDateForTable(fechaObj) {
  const fecha = fechaObj.toString();
  const year = fecha.substring(0, 4);
  const month = fecha.substring(4, 6);
  const day = fecha.substring(6, 8);
  return `${day}/${month}/${year}`;
}
function getFormattedDate(elementId) {
  const dateValue = document.getElementById(elementId).value;
  if (!dateValue) return "";

  const date = new Date(dateValue);
  const year = date.getFullYear();
  const month = String(date.getMonth() + 1).padStart(2, "0");
  const day = String(date.getDate()).padStart(2, "0");

  return `${year}${month}${day}`;
}

function clearAll() {
  clearErrors();
  clearForm();
}

function clearErrors() {
  idTipoIncidenteError.textContent = "";
  idTipoIncidente.classList.remove("borde-rojo");

  descripcionError.textContent = "";
  descripcion.classList.remove("borde-rojo");

  fechaIncidenteError.textContent = "";
  document.getElementById("fechaIncidente").style.border = "";

  idAsignacionError.textContent = "";
  idAsignacionSelect.classList.remove("borde-rojo");

  idClienteError.textContent = "";
  idClientesSelect.classList.remove("borde-rojo");
}
function clearForm() {
  idIncidente.value = "";
  idTipoIncidente.selectedIndex = 1;
  descripcion.value = "";
  fechaIncidente.value = "";
  idAsignacionSelect.innerHTML = "";
  idClientesSelect.selectedIndex = 0;
}
function validateNull() {
  let flag = false;
  if (!idClientesSelect.value || idClientesSelect.value.trim() === "") {
    idClienteError.textContent = "Cliente no puede ser nulo";
    idClientesSelect.classList.add("borde-rojo");
    flag = true;
  }
  if (!idAsignacionSelect.value || idAsignacionSelect.value.trim() === "") {
    idAsignacionError.textContent = "Asignación no puede ser nulo";
    idAsignacionSelect.classList.add("borde-rojo");
    flag = true;
  }
  if (!idTipoIncidente.value || idTipoIncidente.value.trim() === "") {
    idTipoIncidenteError.textContent = "Tipo de incidente no puede ser nulo";
    idTipoIncidente.classList.add("borde-rojo");
    flag = true;
  }
  if (!fechaIncidente.value || fechaIncidente.value.trim() === "") {
    fechaIncidenteError.textContent = "Fecha de incidente no puede ser nulo";
    document.getElementById("fechaIncidente").style.border = "2px solid red";
    flag = true;
  }
  if (!descripcion.value || descripcion.value.trim() === "") {
    descripcionError.textContent = "Descripcion no puede ser nulo";
    descripcion.classList.add("borde-rojo");
    flag = true;
  }
  return flag;
}
function fillSelect(selectElement, items, valorProp, getTexto) {
  selectElement.innerHTML = '<option value="">-- Seleccione --</option>';
  items.forEach((item) => {
    const option = document.createElement("option");
    option.value = item[valorProp];
    option.textContent = getTexto(item);
    selectElement.appendChild(option);
  });
}

idClientesSelect.addEventListener("change", () => {
  const clienteSeleccionado = idClientesSelect.value;

  if (!clienteSeleccionado) {
    idAsignacionSelect.innerHTML = "";
    return;
  }

  // Filtra asignaciones relacionados
  const asignacionesFiltradas = asignacionesData.filter(
    (a) => a.idCliente == clienteSeleccionado
  );

  // Llena los selects
  fillSelect(
    idAsignacionSelect,
    asignacionesFiltradas,
    "idAsignacion",
    (asignacion) =>
      `${asignacion.numeroSerie} - ${asignacion.numeroTrabajador} `
  );
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
//========AQUI VAMOS
function setErrorMsgs(result) {
  if (result["IdTipoIncidente"] && result.IdTipoIncidente != "success") {
    idTipoIncidenteError.textContent = result.IdTipoIncidente;
  }
  if (result["Descripcion"] && result.Descripcion != "success") {
    descripcionError.textContent = result.Descripcion;
  }
  if (result["FechaDeIncidente"] && result.FechaDeIncidente != "success") {
    fechaIncidenteError.textContent = result.FechaDeIncidente;
  }
  if (result["IdAsignacion"] && result.IdAsignacion != "success") {
    idAsignacionError.textContent = result.IdAsignacion;
  }
}

function editeIncidente(incidenteString) {
  const elementTop =
    document.getElementById("main").getBoundingClientRect().top +
    window.scrollY;
  window.scrollTo({
    top: elementTop - 46,
    behavior: "smooth",
  });
  clearAll();
  const incidente = JSON.parse(incidenteString);
  idIncidente.value = incidente.idIncidente;
  descripcion.value = incidente.descripcion;
  idClientesSelect.selectedIndex = incidente.idCliente;

  const clienteSeleccionado = idClientesSelect.value;

  if (!clienteSeleccionado) {
    idAsignacionSelect.innerHTML = "";
    return;
  }

  // Filtra empleados y vehículos relacionados
  const asignacionesFiltradas = asignacionesData.filter(
    (a) => a.idCliente == clienteSeleccionado
  );

  // Llena los selects
  fillSelect(
    idAsignacionSelect,
    asignacionesFiltradas,
    "idAsignacion",
    (asignacion) =>
      `${asignacion.numeroSerie} - ${asignacion.numeroTrabajador} `
  );

  idTipoIncidente.selectedIndex = incidente.idTipoIncidente;
  fechaIncidente.value = formatDateForCalendar(incidente.fechaIncidente);
  idAsignacionSelect.selectedIndex = incidente.idAsignacion;
  actualizarButtonIsActive = true;
}

function createTable(incidentes, page = 1) {
  tbody.innerHTML = "";

  const startIndex = (page - 1) * rowsPerPage;
  const endIndex = startIndex + rowsPerPage;
  const paginationIncidentes = incidentes.slice(startIndex, endIndex);

  paginationIncidentes.forEach((incidente) => {
    const row = document.createElement("tr");
    const incidenteString = JSON.stringify(incidente).replace(/"/g, "&quot;");
    row.innerHTML = `
          <td>${incidente.idIncidente}</td>
          <td>${incidente.tipoIncidente}</td>
          <td>${incidente.descripcion}</td>
          <td>${formatDateForTable(incidente.fechaIncidente)}</td>
          <td>${incidente.numeroSerie}</td>
          <td>${incidente.marca}</td>
          <td>${incidente.tipo}</td>
          <td>${incidente.modelo}</td>
          <td>${incidente.estatusVehiculo}</td>
          <td>${incidente.numeroTrabajador}</td>
          <td>${incidente.nombre}</td>  
          <td>${incidente.apellidoPaterno}</td>  
          <td>${incidente.apellidoMaterno}</td>  
          <td>${incidente.estatusTrabajador}</td>  
          <td>${incidente.razonSocial}</td>
                ${
                  window.asesorMode
                    ? ""
                    : `<td><button class="edit-btn" onclick="editeIncidente('${incidenteString}')">Editar</button></td>
                       <td><button class="delete-btn" onclick="deleteIncidente('${incidente.idIncidente}')">Eliminar</button></td>`
                }
              `;

    tbody.appendChild(row);
  });

  renderPagination(incidentes, page);
}

function renderPagination(incidentes, page) {
  const paginationContainer = document.getElementById("paginationDiv");
  paginationContainer.innerHTML = "";

  const pageCount = Math.ceil(incidentes.length / rowsPerPage);

  if (page > 1) {
    const prevButton = document.createElement("button");
    prevButton.textContent = "Anterior";
    prevButton.addEventListener("click", () => {
      createTable(incidentes, page - 1);
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
      createTable(incidentes, i);
    });
    paginationContainer.appendChild(pageButton);
  }

  if (page < pageCount) {
    const nextButton = document.createElement("button");
    nextButton.textContent = "Siguiente";
    nextButton.addEventListener("click", () => {
      createTable(incidentes, page + 1);
    });
    paginationContainer.appendChild(nextButton);
  }
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
        if (usuario.role === "operacion") {
          document.getElementById("emailUserLogged").textContent =
            usuario.email;
          document.getElementById("loader").style.display = "none";
          document.getElementById("contenido").style.visibility = "visible";
        } else if (usuario.role === "asesor") {
          const incidenteForm = document.getElementById("incidenteForm");
          if (incidenteForm) incidenteForm.remove();
          window.asesorMode = true;
          const tableHeader = document.getElementById("tableHeader");
          if (tableHeader && tableHeader.rows.length > 0) {
            const headerRow = tableHeader.rows[0];
            headerRow.deleteCell(-1);
            headerRow.deleteCell(-1);
          }
          const menuLinks = document.querySelectorAll("#mySidebar a");
          if (menuLinks.length > 0) {
            menuLinks[0].remove();
          }
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

function getAllIncidentes() {
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
        incidentesData = result.myArrayList.map((item) => item.map);
        createTable(incidentesData);
      }
    })
    .catch((error) => {
      let errorMsg = error;
    });
}

function getAllAsignaciones() {
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

  fetch(urlAsignaciones, requestOptions)
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
        asignacionesData = result.myArrayList.map((item) => item.map);
      }
    })
    .catch((error) => {
      let errorMsg = error;
    });
}

function getAllClientes() {
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

  fetch(urlClientes, requestOptions)
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
        idClientesSelect.innerHTML = "";
        idClientesSelect.innerHTML =
          '<option value="">-- Seleccione --</option>';
        clientesData = result.myArrayList.map((item) => item.map);
        clientesData.forEach((cliente) => {
          const option = document.createElement("option");
          option.value = cliente.idCliente;
          option.textContent = `${cliente.razonSocial}`;
          idClientesSelect.appendChild(option);
        });
      }
    })
    .catch((error) => {
      let errorMsg = error;
    });
}

function registerIncidente(raw) {
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
        getAllIncidentes();
        getAllClientes();
        getAllAsignaciones();
        clearAll();
      }
    })
    .catch((error) => {
      let errorMsg = error;
    });
}

function deleteIncidente(idIncidente) {
  Swal.fire({
    title: "¿Quieres eliminar este incidente?",
    text: "Esta acción no podrá revertirse.",
    icon: "warning",
    showCancelButton: true,
    confirmButtonColor: "#3085d6",
    cancelButtonColor: "#d33",
    confirmButtonText: "Eliminar definitivamente",
    cancelButtonText: `Cancelar`,
  }).then((result) => {
    if (result.isConfirmed) {
      const raw = JSON.stringify({ idIncidente: idIncidente });

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

            getAllIncidentes();
            getAllClientes();
            getAllAsignaciones();
            clearAll();
          }
        })
        .catch((error) => {
          let errorMsg = error;
        });
    }
  });
}

function updateIncidente(raw) {
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
        getAllIncidentes();
        getAllClientes();
        getAllAsignaciones();
        clearAll();
        actualizarButtonIsActive = false;
      }
    })
    .catch((error) => {
      let errorMsg = error;
    });
}
