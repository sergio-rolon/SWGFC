let segurosData = [];
let vehiculosData = [];
let urlLogged = "/api/usuarios/logged";
let url = "/api/seguros";
let urlVehiculos = "/api/vehiculos/getVehiculosActivos";
let actualizarButtonIsActive = false;
let currentPage = 1;
const rowsPerPage = 5;
const contenedor = document.getElementById("contenedor");
const tbody = document.getElementById("tableBody");

const numeroPolizaError = document.getElementById("numeroPolizaError");
const aseguradoraError = document.getElementById("aseguradoraError");
const fechaInicioError = document.getElementById("fechaInicioError");
const fechaTerminoError = document.getElementById("fechaTerminoError");
const mensualidadError = document.getElementById("mensualidadError");
const comisionError = document.getElementById("comisionError");
const numeroMesesError = document.getElementById("numeroMesesError");
const idTipoEstatusError = document.getElementById("idTipoEstatusError");
const idVehiculoError = document.getElementById("idVehiculoError");

const idSeguro = document.getElementById("idSeguro");
const numeroPoliza = document.getElementById("numeroPoliza");
const aseguradora = document.getElementById("aseguradora");
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

fechaInicio.max = dateToday;
fechaTermino.max = dateToday;

// *********************Execution at start
window.addEventListener("pageshow", function (event) {
  if (event.persisted) {
    window.location.reload();
  }
});

validateLogin();
getAllSeguros();
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
        numeroPoliza: numeroPoliza.value,
        aseguradora: aseguradora.value,
        fechaInicio: getFormattedDate("fechaInicio"),
        fechaTermino: getFormattedDate("fechaTermino"),
        mensualidad: mensualidad.value,
        comision: comision.value,
        numeroMeses: numeroMeses.value,
        idTipoEstatus: idTipoEstatus.value,
        idVehiculo: idVehiculoSelect.value,
      });

      registerSeguro(raw);
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
          idSeguro: idSeguro.value,
          numeroPoliza: numeroPoliza.value,
          aseguradora: aseguradora.value,
          fechaInicio: getFormattedDate("fechaInicio"),
          fechaTermino: getFormattedDate("fechaTermino"),
          mensualidad: mensualidad.value,
          comision: comision.value,
          numeroMeses: numeroMeses.value,
          idTipoEstatus: idTipoEstatus.value,
          idVehiculo: idVehiculoSelect.value,
        });
        updateSeguro(raw);
      }
    } else {
      Swal.fire({
        title: "Operación inválida",
        text: "Elige primero un seguro para editar",
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
    "Id Seguro",
    "Número de póliza",
    "Aseguradora",
    "Fecha de inicio",
    "Fecha de término",
    "Mensualidad",
    "Comisión",
    "Total",
    "Total con IVA",
    "Número de meses",
    "Estatus seguro",
    "Número de serie",
    "Estatus vehículo",
  ];

  const rows = segurosData.map((seguro) => [
    seguro.idSeguro,
    seguro.numeroPoliza,
    seguro.aseguradora,
    formatDateForTable(seguro.fechaInicio),
    formatDateForTable(seguro.fechaTermino),
    seguro.mensualidad,
    seguro.comision,
    seguro.total,
    seguro.totalConIva,
    seguro.numeroMeses,
    seguro.estatusSeguro,
    seguro.numeroSerie,
    seguro.estatusVehiculo,
  ]);

  const workSheet = XLSX.utils.aoa_to_sheet([headers, ...rows]);

  const workBook = XLSX.utils.book_new();
  XLSX.utils.book_append_sheet(workBook, workSheet, "Seguros");

  XLSX.writeFile(workBook, "seguros_reporte.xlsx");
}

function populateSecondDropdown() {
  const secondDropdown = document.getElementById("secondDropdown");
  secondDropdown.innerHTML = "";

  const uniqueAseguradora = [
    ...new Set(segurosData.map((s) => `${s.aseguradora}`)),
  ];

  uniqueAseguradora.forEach((aseguradora) => {
    const label = document.createElement("label");
    label.innerHTML = `
      <input type="checkbox" class="second-filter" value="${aseguradora}" checked /> ${aseguradora}
    `;
    secondDropdown.appendChild(label);
    secondDropdown.appendChild(document.createElement("br"));
  });
}

function filterSelection() {
  const selectedEstatus = Array.from(
    document.querySelectorAll(".estatus-filter:checked")
  ).map((cb) => cb.value);
  const selectedAseguradora = Array.from(
    document.querySelectorAll(".second-filter:checked")
  ).map((cb) => cb.value);

  const filteredSelection = segurosData.filter(
    (seguro) =>
      selectedEstatus.includes(seguro.estatusSeguro) &&
      selectedAseguradora.includes(seguro.aseguradora)
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
  numeroPolizaError.textContent = "";
  numeroPoliza.classList.remove("borde-rojo");

  aseguradoraError.textContent = "";
  aseguradora.classList.remove("borde-rojo");

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
  if (!numeroPoliza.value || numeroPoliza.value.trim() === "") {
    numeroPolizaError.textContent = "Número de poliza no puede ser nulo";
    numeroPoliza.classList.add("borde-rojo");
    flag = true;
  }
  if (!aseguradora.value || aseguradora.value.trim() === "") {
    aseguradoraError.textContent = "Aseguradora no puede ser nulo";
    aseguradora.classList.add("borde-rojo");
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
  numeroPoliza.value = "";
  aseguradora.value = "";
  fechaInicioElement.value = "";
  fechaTerminoElement.value = "";
  mensualidad.value = "";
  comision.value = "";
  numeroMeses.value = "";
  idTipoEstatus.value = "1";
  idSeguro.value = "";
  actualizarButtonIsActive = false;
  populateVehiculoSelect();
}

function setErrorMsgs(result) {
  if (result["numeroPoliza"] && result.numeroPoliza != "success") {
    numeroPolizaError.textContent = result.numeroPoliza;
  }
  if (result["Aseguradora"] && result.Aseguradora != "success") {
    aseguradoraError.textContent = result.Aseguradora;
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

function editeSeguro(seguroString) {
  const elementTop =
    document.getElementById("main").getBoundingClientRect().top +
    window.scrollY;
  window.scrollTo({
    top: elementTop - 46,
    behavior: "smooth",
  });
  clearAll();
  const seguro = JSON.parse(seguroString);
  idSeguro.value = seguro.idSeguro;
  numeroPoliza.value = seguro.numeroPoliza;
  aseguradora.value = seguro.aseguradora;
  fechaInicioElement.value = formatDateForCalendar(seguro.fechaInicio);
  fechaTerminoElement.value = formatDateForCalendar(seguro.fechaTermino);
  mensualidad.value = seguro.mensualidad;
  comision.value = seguro.comision;
  numeroMeses.value = seguro.numeroMeses;
  if (seguro.estatusSeguro == "activo") {
    idTipoEstatus.value = 1;
  } else {
    idTipoEstatus.value = 2;
  }
  const option = document.createElement("option");
  option.value = seguro.idVehiculo;
  option.textContent = `${seguro.numeroSerie}`;
  idVehiculoSelect.appendChild(option);
  idVehiculoSelect.value = seguro.idVehiculo;

  actualizarButtonIsActive = true;
}

function createTable(seguros, page = 1) {
  tbody.innerHTML = "";

  const startIndex = (page - 1) * rowsPerPage;
  const endIndex = startIndex + rowsPerPage;
  const paginatedSeguros = seguros.slice(startIndex, endIndex);

  paginatedSeguros.forEach((seguro) => {
    const row = document.createElement("tr");
    const seguroString = JSON.stringify(seguro).replace(/"/g, "&quot;");
    row.innerHTML = `
          <td>${seguro.idSeguro}</td>
          <td>${seguro.numeroPoliza}</td>
          <td>${seguro.aseguradora}</td>
          <td>${formatDateForTable(seguro.fechaInicio)}</td>
          <td>${formatDateForTable(seguro.fechaTermino)}</td>
          <td>${seguro.mensualidad}</td>
          <td>${seguro.comision}</td>
          <td>${seguro.total}</td>
          <td>${seguro.totalConIva}</td>
          <td>${seguro.numeroMeses}</td>
          <td>${seguro.estatusSeguro}</td>
          <td>${seguro.numeroSerie}</td>
          <td>${seguro.estatusVehiculo}</td>
                ${
                  window.asesorMode
                    ? ""
                    : `<td><button class="edit-btn" onclick="editeSeguro('${seguroString}')">Editar</button></td>
                       <td><button class="delete-btn" onclick="deleteSeguro('${seguro.numeroPoliza}')">Eliminar</button></td>`
                }
              `;

    tbody.appendChild(row);
  });
  renderPagination(seguros, page);
}

function renderPagination(seguros, page) {
  const paginationContainer = document.getElementById("paginationDiv");
  paginationContainer.innerHTML = "";

  const pageCount = Math.ceil(seguros.length / rowsPerPage);

  if (page > 1) {
    const prevButton = document.createElement("button");
    prevButton.textContent = "Anterior";
    prevButton.addEventListener("click", () => {
      createTable(seguros, page - 1);
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
      createTable(seguros, i);
    });
    paginationContainer.appendChild(pageButton);
  }

  if (page < pageCount) {
    const nextButton = document.createElement("button");
    nextButton.textContent = "Siguiente";
    nextButton.addEventListener("click", () => {
      createTable(seguros, page + 1);
    });
    paginationContainer.appendChild(nextButton);
  }
}

function showActiveSeguros() {
  const activos = segurosData.filter((u) => u.estatusSeguro === "activo");
  createTable(activos);
}

function showInactiveSeguros() {
  const noActivos = segurosData.filter((u) => u.estatusSeguro === "inactivo");
  createTable(noActivos);
}

function showAllSeguros() {
  createTable(segurosData);
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
        if (usuario.role === "operacion") {
          document.getElementById("emailUserLogged").textContent =
            usuario.email;
          document.getElementById("loader").style.display = "none";
          document.getElementById("contenido").style.visibility = "visible";
        } else if (usuario.role === "asesor") {
          const seguroForm = document.getElementById("seguroForm");
          if (seguroForm) seguroForm.remove();
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

function getAllSeguros() {
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
        segurosData = result.myArrayList.map((item) => item.map);
        createTable(segurosData);
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

function registerSeguro(raw) {
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
        getAllSeguros();
        getAllVehiculos();
        clearAll();
      }
    })
    .catch((error) => {
      let errorMsg = error;
    });
}

function deleteSeguro(numeroPoliza) {
  Swal.fire({
    title: "¿Quieres eliminar este seguro?",
    text: "Esta acción no podrá revertirse.",
    icon: "warning",
    showCancelButton: true,
    confirmButtonColor: "#3085d6",
    cancelButtonColor: "#d33",
    confirmButtonText: "Eliminar definitivamente",
    cancelButtonText: `Cancelar`,
  }).then((result) => {
    if (result.isConfirmed) {
      const raw = JSON.stringify({ numeroPoliza: numeroPoliza });

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
            getAllSeguros();
            getAllVehiculos();
          }
        })
        .catch((error) => {
          let errorMsg = error;
        });
    }
  });
}

function updateSeguro(raw) {
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
        getAllSeguros();
        getAllVehiculos();
        clearAll();
        actualizarButtonIsActive = false;
      }
    })
    .catch((error) => {
      let errorMsg = error;
    });
}
