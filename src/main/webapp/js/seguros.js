let segurosData = [];
let urlLogged = "/api/usuarios/logged";
let url = "/api/seguros";
let urlVehiculos = "/api/vehiculos/getVehiculosSinSeguro";
let actualizarButtonIsActive = false;

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
  if (idVehiculoSelect.options.length > 0) {
    idVehiculoSelect.selectedIndex = 0;
  }
  idSeguro.value = "";
  actualizarButtonIsActive = false;
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

function createTable(seguros) {
  tbody.innerHTML = "";
  seguros.forEach((seguro) => {
    const row = document.createElement("tr");
    const seguroString = JSON.stringify(seguro).replace(/"/g, "&quot;");
    row.innerHTML = `
          <td>${seguro.idSeguro}</td>
          <td>${seguro.numeroPoliza}</td>
          <td>${seguro.aseguradora}</td>
          <td>${seguro.fechaInicio}</td>
          <td>${seguro.fechaTermino}</td>
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
        idVehiculoSelect.innerHTML = "";
        let vehiculos = result.myArrayList.map((item) => item.map);
        vehiculos.forEach((vehiculo) => {
          const option = document.createElement("option");
          option.value = vehiculo.idVehiculo;
          option.textContent = `${vehiculo.numeroSerie}`;
          idVehiculoSelect.appendChild(option);
        });
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
