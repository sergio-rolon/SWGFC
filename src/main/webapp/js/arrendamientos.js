let arrendamientosData = [];
let vehiculosData = [];
let rowsFiltered = [];
let urlLogged = "/api/usuarios/logged";
let url = "/api/arrendamientos";
let urlVehiculos = "/api/vehiculos/getVehiculosSinArrendamiento";
let actualizarButtonIsActive = false;
let currentPage = 1;
const rowsPerPage = 5;
const contenedor = document.getElementById("contenedor");
const tbody = document.getElementById("tableBody");

const numeroContratoError = document.getElementById("numeroContratoError");
const arrendadoraError = document.getElementById("arrendadoraError");
const fechaInicioError = document.getElementById("fechaInicioError");
const fechaTerminoError = document.getElementById("fechaTerminoError");
const mensualidadError = document.getElementById("mensualidadError");
const comisionError = document.getElementById("comisionError");
const numeroMesesError = document.getElementById("numeroMesesError");
const idTipoEstatusError = document.getElementById("idTipoEstatusError");
const idVehiculoError = document.getElementById("idVehiculoError");

const idArrendamiento = document.getElementById("idArrendamiento");
const numeroContrato = document.getElementById("numeroContrato");
const arrendadora = document.getElementById("arrendadora");
const mensualidad = document.getElementById("mensualidad");
const comision = document.getElementById("comision");
const numeroMeses = document.getElementById("numeroMeses");
const idVehiculo = document.getElementById("idVehiculo");
const idTipoEstatus = document.getElementById("idTipoEstatus");
const idVehiculoSelect = document.getElementById("idVehiculoSelect");
const fechaInicioElement = document.getElementById("fechaInicio");
const fechaTerminoElement = document.getElementById("fechaTermino");

const today = new Date();

const yyyy = today.getFullYear();
const mm = String(today.getMonth() + 1).padStart(2, "0");
const dd = String(today.getDate()).padStart(2, "0");
const dateToday = `${yyyy}-${mm}-${dd}`;

fechaInicio.min = "2000-01-01";
fechaTermino.max = "2040-01-01";

// *********************Execution at start
window.addEventListener("pageshow", function (event) {
  if (event.persisted) {
    window.location.reload();
  }
});

validateLogin();
getAllArrendamientos();
getAllVehiculos();

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
        numeroContrato: numeroContrato.value,
        arrendadora: arrendadora.value,
        fechaInicio: getFormattedDate("fechaInicio"),
        fechaTermino: getFormattedDate("fechaTermino"),
        mensualidad: mensualidad.value,
        comision: comision.value,
        numeroMeses: numeroMeses.value,
        idTipoEstatus: idTipoEstatus.value,
        idVehiculo: idVehiculoSelect.value,
      });

      registerArrendamiento(raw);
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
          idArrendamiento: idArrendamiento.value,
          numeroContrato: numeroContrato.value,
          arrendadora: arrendadora.value,
          fechaInicio: getFormattedDate("fechaInicio"),
          fechaTermino: getFormattedDate("fechaTermino"),
          mensualidad: mensualidad.value,
          comision: comision.value,
          numeroMeses: numeroMeses.value,
          idTipoEstatus: idTipoEstatus.value,
          idVehiculo: idVehiculoSelect.value,
        });
        updateArrendamiento(raw);
      }
    } else {
      Swal.fire({
        title: "Operación inválida",
        text: "Elige primero un arrendamiento para editar",
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
fechaInicioElement.addEventListener("change", dateValidation);
fechaTerminoElement.addEventListener("change", dateValidation);
//************************************** Functions
function populateVehiculoSelect() {
  idVehiculoSelect.innerHTML = "";
  vehiculosData.forEach((vehiculo) => {
    const option = document.createElement("option");
    option.value = vehiculo.idVehiculo;
    option.textContent = `${vehiculo.numeroSerie}`;
    idVehiculoSelect.appendChild(option);
  });
}
function exportToXlsx() {
  const headers = [
    "Número de contrato",
    "Arrendadora",
    "Fecha de inicio",
    "Fecha de término",
    "Mensualidad",
    "Comisión",
    "Total",
    "Total con IVA",
    "Número de meses",
    "Estatus arrendamiento",
    "Número de serie",
    "Estatus vehículo",
  ];

  const rows = arrendamientosData.map((arrendamiento) => [
    arrendamiento.numeroContrato,
    arrendamiento.arrendadora,
    formatDateForTable(arrendamiento.fechaInicio),
    formatDateForTable(arrendamiento.fechaTermino),
    arrendamiento.mensualidad,
    arrendamiento.comision,
    arrendamiento.total,
    arrendamiento.totalConIva,
    arrendamiento.numeroMeses,
    arrendamiento.estatusArrendamiento,
    arrendamiento.numeroSerie,
    arrendamiento.estatusVehiculo,
  ]);

  const workSheet = XLSX.utils.aoa_to_sheet([headers, ...rows]);

  const workBook = XLSX.utils.book_new();
  XLSX.utils.book_append_sheet(workBook, workSheet, "Arrendamientos");

  XLSX.writeFile(workBook, "arrendamientos_reporte.xlsx");
}

function populateSecondDropdown() {
  const secondDropdown = document.getElementById("secondDropdown");
  secondDropdown.innerHTML = "";

  const uniqueArrendadora = [
    ...new Set(arrendamientosData.map((a) => `${a.arrendadora}`)),
  ];

  uniqueArrendadora.forEach((arrendadora) => {
    const label = document.createElement("label");
    label.innerHTML = `
      <input type="checkbox" class="second-filter" value="${arrendadora}" checked /> ${arrendadora}
    `;
    secondDropdown.appendChild(label);
    secondDropdown.appendChild(document.createElement("br"));
  });
}

function filterSelection() {
  const selectedEstatus = Array.from(
    document.querySelectorAll(".estatus-filter:checked")
  ).map((cb) => cb.value);
  const selectedArrendadora = Array.from(
    document.querySelectorAll(".second-filter:checked")
  ).map((cb) => cb.value);

  const filteredSelection = arrendamientosData.filter(
    (arrendamiento) =>
      selectedEstatus.includes(arrendamiento.estatusArrendamiento) &&
      selectedArrendadora.includes(arrendamiento.arrendadora)
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

function dateValidation() {
  fechaTerminoError.textContent = "";
  document.getElementById("fechaTermino").style.border = "";

  const inicio = fechaInicioElement.value;
  const termino = fechaTerminoElement.value;

  if (inicio && termino) {
    const fechaInicio = new Date(inicio);
    const fechaTermino = new Date(termino);

    if (fechaTermino < fechaInicio) {
      fechaTerminoError.textContent =
        "Fecha de término debe ser mayor a fecha de inicio.";
      document.getElementById("fechaTermino").style.border = "2px solid red";
    }
  }
}
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
  numeroContratoError.textContent = "";
  numeroContrato.classList.remove("borde-rojo");

  arrendadoraError.textContent = "";
  arrendadora.classList.remove("borde-rojo");

  fechaInicioError.textContent = "";
  fechaInicioElement.classList.remove("borde-rojo");
  document.getElementById("fechaInicio").style.border = "";
  fechaTerminoError.textContent = "";
  document.getElementById("fechaTermino").style.border = "";

  mensualidadError.textContent = "";
  mensualidad.classList.remove("borde-rojo");

  comisionError.textContent = "";
  comision.classList.remove("borde-rojo");

  numeroMesesError.textContent = "";
  numeroMeses.classList.remove("borde-rojo");
}

function validateNull() {
  let flag = false;
  if (!numeroContrato.value || numeroContrato.value.trim() === "") {
    numeroContratoError.textContent = "Número de contrato no puede ser nulo";
    numeroContrato.classList.add("borde-rojo");
    flag = true;
  }
  if (!arrendadora.value || arrendadora.value.trim() === "") {
    arrendadoraError.textContent = "Arrendadora no puede ser nulo";
    arrendadora.classList.add("borde-rojo");
    flag = true;
  }
  if (!fechaInicioElement.value || fechaInicioElement.value.trim() === "") {
    fechaInicioError.textContent = "Fecha de inicio no puede ser nulo";
    document.getElementById("fechaInicio").style.border = "2px solid red";
    flag = true;
  }
  if (!fechaTerminoElement.value || fechaTerminoElement.value.trim() === "") {
    fechaTerminoError.textContent = "Fecha de término no puede ser nulo";
    document.getElementById("fechaTermino").style.border = "2px solid red";
    flag = true;
  }
  if (!mensualidad.value || mensualidad.value.trim() === "") {
    mensualidadError.textContent = "Mensualidad no puede ser nulo";
    mensualidad.classList.add("borde-rojo");
    flag = true;
  }
  if (!comision.value || comision.value.trim() === "") {
    comisionError.textContent = "Comisión no puede ser nulo";
    comision.classList.add("borde-rojo");
    flag = true;
  }
  if (!numeroMeses.value || numeroMeses.value.trim() === "") {
    numeroMesesError.textContent = "Número de meses no puede ser nulo";
    numeroMeses.classList.add("borde-rojo");
    flag = true;
  }
  return flag;
}

function clearForm() {
  numeroContrato.value = "";
  arrendadora.value = "";
  fechaInicioElement.value = "";
  fechaTerminoElement.value = "";
  mensualidad.value = "";
  comision.value = "";
  numeroMeses.value = "";
  idTipoEstatus.value = "1";
  actualizarButtonIsActive = false;
  populateVehiculoSelect();
}

function setErrorMsgs(result) {
  if (result["numeroContrato"] && result.numeroContrato != "success") {
    numeroContratoError.textContent = result.numeroContrato;
  }
  if (result["Arrendadora"] && result.Arrendadora != "success") {
    arrendadoraError.textContent = result.Arrendadora;
  }
  if (result["Fechadeinicio"] && result.Fechadeinicio != "success") {
    fechaInicioError.textContent = result.Fechadeinicio;
  }
  if (result["Fechadetermino"] && result.Fechadetermino != "success") {
    fechaTerminoError.textContent = result.Fechadetermino;
  }
  if (result["Mensualidad"] && result.Mensualidad != "success") {
    mensualidadError.textContent = result.Mensualidad;
  }
  if (result["Comision"] && result.Comision != "success") {
    comisionError.textContent = result.Comision;
  }
  if (result["Numerodemeses"] && result.Numerodemeses != "success") {
    numeroMesesError.textContent = result.Numerodemeses;
  }
  if (result["IdTipoEstatus"] && result.IdTipoEstatus != "success") {
    idTipoEstatusError.textContent = result.IdTipoEstatus;
  }
  if (result["IdVehiculo"] && result.IdVehiculo != "success") {
    idVehiculoError.textContent = result.IdVehiculo;
  }
}

function editeArrendamiento(arrendamientoString) {
  document.getElementById("btnRegistrar").style.display = "none";
  document.getElementById("btnActualizar").style.display = "block";
  document.getElementById("modal-title").textContent = "Actualizar";
  bsModal.show();
  const elementTop =
    document.getElementById("main").getBoundingClientRect().top +
    window.scrollY;
  window.scrollTo({
    top: elementTop - 46,
    behavior: "smooth",
  });
  clearAll();
  const arrendamiento = JSON.parse(arrendamientoString);
  idArrendamiento.value = arrendamiento.idArrendamiento;
  numeroContrato.value = arrendamiento.numeroContrato;
  arrendadora.value = arrendamiento.arrendadora;
  fechaInicioElement.value = formatDateForCalendar(arrendamiento.fechaInicio);
  fechaTerminoElement.value = formatDateForCalendar(arrendamiento.fechaTermino);
  mensualidad.value = arrendamiento.mensualidad;
  comision.value = arrendamiento.comision;
  numeroMeses.value = arrendamiento.numeroMeses;
  if (arrendamiento.estatusArrendamiento == "Activo") {
    idTipoEstatus.value = 1;
  } else {
    idTipoEstatus.value = 2;
  }
  const option = document.createElement("option");
  option.value = arrendamiento.idVehiculo;
  option.textContent = `${arrendamiento.numeroSerie}`;
  idVehiculoSelect.appendChild(option);
  idVehiculoSelect.value = arrendamiento.idVehiculo;

  actualizarButtonIsActive = true;
}

function createTable(arrendamientos, page = 1) {
  tbody.innerHTML = "";

  const startIndex = (page - 1) * rowsPerPage;
  const endIndex = startIndex + rowsPerPage;
  const paginatedArrendamientos = arrendamientos.slice(startIndex, endIndex);

  paginatedArrendamientos.forEach((arrendamiento) => {
    const row = document.createElement("tr");
    const arrendamientoString = JSON.stringify(arrendamiento).replace(
      /"/g,
      "&quot;"
    );
    row.innerHTML = `
          <td>${arrendamiento.numeroContrato}</td>
          <td>${arrendamiento.arrendadora}</td>
          <td>${formatDateForTable(arrendamiento.fechaInicio)}</td>
          <td>${formatDateForTable(arrendamiento.fechaTermino)}</td>
          <td>${arrendamiento.mensualidad}</td>
          <td>${arrendamiento.comision}</td>
          <td>${arrendamiento.total}</td>
          <td>${arrendamiento.totalConIva}</td>
          <td>${arrendamiento.numeroMeses}</td>
          <td>${arrendamiento.estatusArrendamiento}</td>
          <td>${arrendamiento.numeroSerie}</td>
          <td>${arrendamiento.estatusVehiculo}</td>
                ${
                  window.asesorMode
                    ? ""
                    : `<td><button class="edit-btn" onclick="editeArrendamiento('${arrendamientoString}')"><img src="/images/edit-button.png" alt="Editar" class="edite-icon"></button></td>
                       <td><button class="delete-btn" onclick="deleteArrendamiento('${arrendamiento.numeroContrato}')"><img src="/images/delete.png" alt="Eliminar" class="delete-icon"></button></td>`
                }
              `;

    tbody.appendChild(row);
  });
  renderPagination(arrendamientos, page);
}

function renderPagination(arrendamientos, page) {
  const paginationContainer = document.getElementById("paginationDiv");
  paginationContainer.innerHTML = "";

  const pageCount = Math.ceil(arrendamientos.length / rowsPerPage);

  if (page > 1) {
    const prevButton = document.createElement("button");
    prevButton.textContent = "Anterior";
    prevButton.addEventListener("click", () => {
      createTable(arrendamientos, page - 1);
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
      createTable(arrendamientos, i);
    });
    paginationContainer.appendChild(pageButton);
  }

  if (page < pageCount) {
    const nextButton = document.createElement("button");
    nextButton.textContent = "Siguiente";
    nextButton.addEventListener("click", () => {
      createTable(arrendamientos, page + 1);
    });
    paginationContainer.appendChild(nextButton);
  }
}

function showActiveArrendamientos() {
  const activos = arrendamientosData.filter(
    (u) => u.estatusArrendamiento === "Activo"
  );
  createTable(activos);
}

function showInactiveArrendamientos() {
  const noActivos = arrendamientosData.filter(
    (u) => u.estatusArrendamiento === "Inactivo"
  );
  createTable(noActivos);
}

function showAllArrendamientos() {
  const checkboxes1 = document.querySelectorAll(".estatus-filter");
  checkboxes1.forEach((chk) => (chk.checked = true));

  const checkboxes2 = document.querySelectorAll(".second-filter");
  checkboxes2.forEach((chk) => (chk.checked = true));

  createTable(arrendamientosData);
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
          document.getElementById("usuariosMenu").style.display = "block";
        }
        if (usuario.role === "Operación" || usuario.role === "Administrador") {
          document.getElementById("emailUserLogged").textContent =
            usuario.email;
          document.getElementById("loader").style.display = "none";
          document.getElementById("contenido").style.visibility = "visible";
        } else if (usuario.role === "Asesor") {
          btnAbrirModal.style.display = "none";
          const arrendamientoForm =
            document.getElementById("arrendamientoForm");
          if (arrendamientoForm) arrendamientoForm.remove();
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
            menuLinks[1].remove();
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

function getAllArrendamientos() {
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
        arrendamientosData = result.myArrayList.map((item) => item.map);
        rowsFiltered = [...arrendamientosData];
        createTable(arrendamientosData);
        populateSecondDropdown();
      }
    })
    .catch((error) => {
      let errorMsg = error;
    });
}

function getAllVehiculos() {
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

  fetch(urlVehiculos, requestOptions)
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
        vehiculosData = result.myArrayList.map((item) => item.map);
        populateVehiculoSelect();
      }
    })
    .catch((error) => {
      let errorMsg = error;
    });
}

function registerArrendamiento(raw) {
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
        getAllArrendamientos();
        getAllVehiculos();
        clearAll();
      }
    })
    .catch((error) => {
      let errorMsg = error;
    });
}

function deleteArrendamiento(numeroContrato) {
  Swal.fire({
    title: "¿Quieres eliminar este arrendamiento?",
    text: "Esta acción no podrá revertirse.",
    icon: "warning",
    showCancelButton: true,
    confirmButtonColor: "#3085d6",
    cancelButtonColor: "#d33",
    confirmButtonText: "Eliminar definitivamente",
    cancelButtonText: `Cancelar`,
  }).then((result) => {
    if (result.isConfirmed) {
      const raw = JSON.stringify({ numeroContrato: numeroContrato });

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
            getAllArrendamientos();
            getAllVehiculos();
          }
        })
        .catch((error) => {
          let errorMsg = error;
        });
    }
  });
}

function updateArrendamiento(raw) {
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
        getAllArrendamientos();
        getAllVehiculos();
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

  rowsFiltered = arrendamientosData.filter((arrendamiento) =>
    arrendamiento.numeroSerie.toLowerCase().includes(searchedValue)
  );

  createTable(rowsFiltered, 1);
});
const modalEl = document.getElementById("formModal");
const bsModal = new bootstrap.Modal(modalEl);

document.getElementById("btnAbrirModal").addEventListener("click", () => {
  document.getElementById("btnActualizar").style.display = "none";
  document.getElementById("btnRegistrar").style.display = "block";
  document.getElementById("modal-title").textContent = "Registrar";
  bsModal.show();
});
