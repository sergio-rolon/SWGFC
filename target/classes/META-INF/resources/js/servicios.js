let serviciosData = [];
let clientesData = [];
let asignacionesData = [];
let urlLogged = "/api/usuarios/logged";
let url = "/api/servicios";
let urlAsignaciones = "/api/asignaciones/getAsignacionesParaServicios";
let urlClientes = "/api/clientes/getAllClientes";
let actualizarButtonIsActive = false;
let currentPage = 1;
const rowsPerPage = 5;
const contenedor = document.getElementById("contenedor");
const tbody = document.getElementById("tableBody");

const idTipoServicioError = document.getElementById("idTipoServicioError");
const kilometrajeError = document.getElementById("kilometrajeError");
const fechaServicioError = document.getElementById("fechaServicioError");
const costoError = document.getElementById("costoError");
const comisionError = document.getElementById("comisionError");
const idAsignacionError = document.getElementById("idAsignacionError");

const idServicio = document.getElementById("idServicio");
const idTipoServicio = document.getElementById("idTipoServicio");
const fechaServicio = document.getElementById("fechaServicio");
const kilometraje = document.getElementById("kilometraje");
const costo = document.getElementById("costo");
const comision = document.getElementById("comision");

const idEmpleadoSelect = document.getElementById("idEmpleadoSelect");
const idAsignacionSelect = document.getElementById("idAsignacionSelect");
// *********************Execution at start
window.addEventListener("pageshow", function (event) {
  if (event.persisted) {
    window.location.reload();
  }
});

validateLogin();
getAllServicios();
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
        idTipoServicio: idTipoServicio.value,
        kilometraje: kilometraje.value,
        fechaServicio: getFormattedDate("fechaServicio"),
        costo: costo.value,
        comision: comision.value,
        idAsignacion: idAsignacionSelect.value,
      });

      registerServicio(raw);
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
          idServicio: idServicio.value,
          idTipoServicio: idTipoServicio.value,
          kilometraje: kilometraje.value,
          fechaServicio: getFormattedDate("fechaServicio"),
          costo: costo.value,
          comision: comision.value,
          idAsignacion: idAsignacionSelect.value,
        });
        updateServicio(raw);
      }
    } else {
      Swal.fire({
        title: "Operación inválida",
        text: "Elige primero un servicio para editar",
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
  idTipoServicioError.textContent = "";
  idTipoServicio.classList.remove("borde-rojo");

  kilometrajeError.textContent = "";
  kilometraje.classList.remove("borde-rojo");

  fechaServicioError.textContent = "";
  document.getElementById("fechaServicio").style.border = "";

  costoError.textContent = "";
  costo.classList.remove("borde-rojo");

  comisionError.textContent = "";
  comision.classList.remove("borde-rojo");

  idAsignacionError.textContent = "";
  idAsignacionSelect.classList.remove("borde-rojo");

  idClienteError.textContent = "";
  idClientesSelect.classList.remove("borde-rojo");
}
function clearForm() {
  idServicio.value = "";
  idTipoServicio.selectedIndex = 1;
  kilometraje.value = "";
  fechaServicio.value = "";
  costo.value = "";
  comision.value = "";
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
  if (!idTipoServicio.value || idTipoServicio.value.trim() === "") {
    idTipoServicioError.textContent = "Tipo de servicio no puede ser nulo";
    idTipoServicio.classList.add("borde-rojo");
    flag = true;
  }
  if (!fechaServicio.value || fechaServicio.value.trim() === "") {
    fechaServicioError.textContent = "Fecha de servicio no puede ser nulo";
    document.getElementById("fechaServicio").style.border = "2px solid red";
    flag = true;
  }
  if (!kilometraje.value || kilometraje.value.trim() === "") {
    kilometrajeError.textContent = "Kilometraje no puede ser nulo";
    kilometraje.classList.add("borde-rojo");
    flag = true;
  }
  if (!costo.value || costo.value.trim() === "") {
    costoError.textContent = "Costo no puede ser nulo";
    costo.classList.add("borde-rojo");
    flag = true;
  }
  if (!comision.value || comision.value.trim() === "") {
    comisionError.textContent = "Comisión no puede ser nulo";
    comision.classList.add("borde-rojo");
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
  if (result["IdTipoServicio"] && result.IdTipoServicio != "success") {
    idTipoServicioError.textContent = result.IdTipoServicio;
  }
  if (result["Kilometraje"] && result.Kilometraje != "success") {
    kilometrajeError.textContent = result.Kilometraje;
  }
  if (result["FechaDeServicio"] && result.FechaDeServicio != "success") {
    fechaServicioError.textContent = result.FechaDeServicio;
  }
  if (result["Costo"] && result.Costo != "success") {
    costoError.textContent = result.Costo;
  }
  if (result["Comision"] && result.Comision != "success") {
    comisionError.textContent = result.Comision;
  }
  if (result["IdAsignacion"] && result.IdAsignacion != "success") {
    idAsignacionError.textContent = result.IdAsignacion;
  }
}
/*function setSelectedByValue(selectElement, value) {
  for (let i = 0; i < selectElement.options.length; i++) {
    if (selectElement.options[i].textContent == value) {
      selectElement.selectedIndex = i;
      return;
    }
  }
}*/

function editeServicio(servicioString) {
  const elementTop =
    document.getElementById("main").getBoundingClientRect().top +
    window.scrollY;
  window.scrollTo({
    top: elementTop - 46,
    behavior: "smooth",
  });
  clearAll();
  const servicio = JSON.parse(servicioString);
  idServicio.value = servicio.idServicio;
  kilometraje.value = servicio.kilometraje;
  idClientesSelect.selectedIndex = servicio.idCliente;

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

  idTipoServicio.value = servicio.idTipoServicio;
  fechaServicio.value = formatDateForCalendar(servicio.fechaServicio);
  costo.value = servicio.costo;
  comision.value = servicio.comision;
  idAsignacionSelect.selectedIndex = servicio.idAsignacion;
  actualizarButtonIsActive = true;
}

function createTable(servicios, page = 1) {
  tbody.innerHTML = "";

  const startIndex = (page - 1) * rowsPerPage;
  const endIndex = startIndex + rowsPerPage;
  const paginatedServicios = servicios.slice(startIndex, endIndex);

  paginatedServicios.forEach((servicio) => {
    const row = document.createElement("tr");
    const servicioString = JSON.stringify(servicio).replace(/"/g, "&quot;");
    row.innerHTML = `
          <td>${servicio.idServicio}</td>
          <td>${servicio.tipoServicio}</td>
          <td>${servicio.kilometraje}</td>
          <td>${formatDateForTable(servicio.fecha)}</td>
          <td>${servicio.costo}</td>
          <td>${servicio.comision}</td>
          <td>${servicio.total}</td>
          <td>${servicio.totalConIva}</td>
          <td>${servicio.numeroSerie}</td>
          <td>${servicio.marca}</td>
          <td>${servicio.tipo}</td>
          <td>${servicio.modelo}</td>
          <td>${servicio.estatusVehiculo}</td>
          <td>${servicio.numeroTrabajador}</td>
          <td>${servicio.nombre}</td>  
          <td>${servicio.apellidoPaterno}</td>  
          <td>${servicio.apellidoMaterno}</td>  
          <td>${servicio.estatusTrabajador}</td>  
          <td>${servicio.razonSocial}</td>
                ${
                  window.asesorMode
                    ? ""
                    : `<td><button class="edit-btn" onclick="editeServicio('${servicioString}')">Editar</button></td>
                       <td><button class="delete-btn" onclick="deleteServicio('${servicio.idServicio}')">Eliminar</button></td>`
                }
              `;

    tbody.appendChild(row);
  });
  renderPagination(servicios, page);
}

function renderPagination(servicios, page) {
  const paginationContainer = document.getElementById("paginationDiv");
  paginationContainer.innerHTML = "";

  const pageCount = Math.ceil(servicios.length / rowsPerPage);

  if (page > 1) {
    const prevButton = document.createElement("button");
    prevButton.textContent = "Anterior";
    prevButton.addEventListener("click", () => {
      createTable(servicios, page - 1);
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
      createTable(servicios, i);
    });
    paginationContainer.appendChild(pageButton);
  }

  if (page < pageCount) {
    const nextButton = document.createElement("button");
    nextButton.textContent = "Siguiente";
    nextButton.addEventListener("click", () => {
      createTable(servicios, page + 1);
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
          const servicioForm = document.getElementById("servicioForm");
          if (servicioForm) servicioForm.remove();
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

function getAllServicios() {
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
        serviciosData = result.myArrayList.map((item) => item.map);
        createTable(serviciosData);
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

function registerServicio(raw) {
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
        getAllServicios();
        getAllClientes();
        getAllAsignaciones();
        clearAll();
      }
    })
    .catch((error) => {
      let errorMsg = error;
    });
}

function deleteServicio(idServicio) {
  Swal.fire({
    title: "¿Quieres eliminar este servicio?",
    text: "Esta acción no podrá revertirse.",
    icon: "warning",
    showCancelButton: true,
    confirmButtonColor: "#3085d6",
    cancelButtonColor: "#d33",
    confirmButtonText: "Eliminar definitivamente",
    cancelButtonText: `Cancelar`,
  }).then((result) => {
    if (result.isConfirmed) {
      const raw = JSON.stringify({ idServicio: idServicio });

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

            getAllServicios();
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

function updateServicio(raw) {
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
        getAllServicios();
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
