let arrendamientosData = [];
let urlLogged = "/api/usuarios/logged";
let url = "/api/arrendamientos";
let urlVehiculos = "/api/vehiculos/getAllVehiculos";
let actualizarButtonIsActive = false;

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
const fechaInicio = document.getElementById("fechaInicio");
const fechaTermino = document.getElementById("fechaTermino");
const mensualidad = document.getElementById("mensualidad");
const comision = document.getElementById("comision");
const numeroMeses = document.getElementById("numeroMeses");
const idVehiculo = document.getElementById("idVehiculo");
const idTipoEstatus = document.getElementById("idTipoEstatus");
const idVehiculoSelect = document.getElementById("idVehiculoSelect");
// *********************Execution at start
window.addEventListener('pageshow', function (event) {
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
      fechaInicio: fechaInicio.value,
      fechaTermino: fechaTermino.value,
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
          fechaInicio: fechaInicio.value,
          fechaTermino: fechaTermino.value,
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

//************************************** Functions
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
  fechaInicio.classList.remove("borde-rojo");

  fechaTerminoError.textContent = "";
  fechaTermino.classList.remove("borde-rojo");

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
  if (!fechaInicio.value || fechaInicio.value.trim() === "") {
    fechaInicioError.textContent = "Fecha de inicio no puede ser nulo";
    fechaInicio.classList.add("borde-rojo");
    flag = true;
  }
  if (!fechaTermino.value || fechaTermino.value.trim() === "") {
    fechaTerminoError.textContent = "Fecha de término no puede ser nulo";
    fechaTermino.classList.add("borde-rojo");
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
  fechaInicio.value = "";
  fechaTermino.value = "";
  mensualidad.value = "";
  comision.value = "";
  numeroMeses.value = "";
  idTipoEstatus.value = "1";
    if (idVehiculoSelect.options.length > 0) {
      idVehiculoSelect.selectedIndex = 0;
    }
  idArrendamiento.value = "";
  actualizarButtonIsActive = false;
}

function setErrorMsgs(result) {
  if (result["numeroContrato"] && result.numeroContrato != "success") {
    numeroContratoError.textContent = result.numeroContrato;
  }
  if (result["Arrendadora"] && result.Arrendadora != "success") {
    arrendadoraError.textContent = result.Arrendadora;
  }
  if (result["FechaDeInicio"] && result.FechaDeInicio != "success") {
    fechaInicioError.textContent = result.FechaDeInicio;
  }
  if (result["FechaDeTermino"] && result.FechaDeTermino != "success") {
    fechaTerminoError.textContent = result.FechaDeTermino;
  }
  if (result["Mensualidad"] && result.Mensualidad != "success") {
    mensualidadError.textContent = result.Mensualidad;
  }
  if (result["Comision"] && result.Comision != "success") {
    comisionError.textContent = result.Comision;
  }
  if (result["NumeroDeMeses"] && result.NumeroDeMeses != "success") {
    numeroMesesError.textContent = result.NumeroDeMeses;
  }
  if (result["IdTipoEstatus"] && result.IdTipoEstatus != "success") {
    idTipoEstatusError.textContent = result.IdTipoEstatus;
  }
  if (result["IdVehiculo"] && result.IdVehiculo != "success") {
    idVehiculoError.textContent = result.IdVehiculo;
  }
}

function editeArrendamiento(arrendamientoString) {
  clearAll();
  const arrendamiento = JSON.parse(arrendamientoString);
  idArrendamiento.value = arrendamiento.idArrendamiento;
  numeroContrato.value=arrendamiento.numeroContrato;
  arrendadora.value=arrendamiento.arrendadora;
  fechaInicio.value=arrendamiento.fechaInicio;
  fechaTermino.value = arrendamiento.fechaTermino;
  mensualidad.value = arrendamiento.mensualidad;
  comision.value = arrendamiento.comision;
  numeroMeses.value = arrendamiento.numeroMeses;
  if (arrendamiento.estatusArrendamiento == "activo") {
    idTipoEstatus.value = 1;
  } else {
    idTipoEstatus.value = 2;
  }
  idVehiculoSelect.value = vehiculo.idVehiculo;

  actualizarButtonIsActive = true;
}

function createTable(arrendamientos) {
  tbody.innerHTML = "";
  arrendamientos.forEach((arrendamiento) => {
    const row = document.createElement("tr");
    const arrendamientoString = JSON.stringify(arrendamiento).replace(/"/g, "&quot;");
    row.innerHTML = `
          <td>${arrendamiento.idArrendamiento}</td>
          <td>${arrendamiento.numeroContrato}</td>
          <td>${arrendamiento.arrendadora}</td>
          <td>${arrendamiento.fechaInicio}</td>
          <td>${arrendamiento.fechaTermino}</td>
          <td>${arrendamiento.mensualidad}</td>
          <td>${arrendamiento.comision}</td>
          <td>${arrendamiento.numeroMeses}</td>
          <td>${arrendamiento.estatusArrendamiento}</td>
          <td>${arrendamiento.numeroSerie}</td>
          <td>${arrendamiento.estatusVehiculo}</td>
                ${
                  window.asesorMode
                    ? ""
                    : `<td><button class="edit-btn" onclick="editeArrendamiento('${arrendamientoString}')">Editar</button></td>
                       <td><button class="delete-btn" onclick="deleteArrendamiento('${arrendamiento.numeroContrato}')">Eliminar</button></td>`
                }
              `;

    tbody.appendChild(row);
  });
}

function showActiveArrendamientos() {
  const activos = arrendamientosData.filter((u) => u.estatusArrendamiento === "activo");
  createTable(activos);
}

function showInactiveArrendamientos() {
  const noActivos = arrendamientosData.filter((u) => u.estatusArrendamiento === "inactivo");
  createTable(noActivos);
}

function showAllArrendamientos() {
  createTable(arrendamientosData);
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
          document.getElementById("emailUserLogged").textContent = usuario.email;
          document.getElementById("loader").style.display = "none";
          document.getElementById("contenido").style.visibility = "visible";
        } else if(usuario.role === "asesor"){
          const vehiculoForm = document.getElementById("vehiculoForm");
          if (vehiculoForm) vehiculoForm.remove();
          window.asesorMode = true;
          const tableHeader = document.getElementById("tableHeader");
            if (tableHeader && tableHeader.rows.length > 0) {
              const headerRow = tableHeader.rows[0];
              headerRow.deleteCell(-1);
              headerRow.deleteCell(-1);
            }
          document.getElementById("emailUserLogged").textContent = usuario.email;
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
        createTable(arrendamientosData);
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
        clearAll();
        actualizarButtonIsActive = false;
      }
    })
    .catch((error) => {
      let errorMsg = error;
    });
}
