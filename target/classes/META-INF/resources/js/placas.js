let placasData = [];
let urlLogged = "/api/usuarios/logged";
let url = "/api/placas";
let urlVehiculos = "/api/vehiculos/getVehiculosSinPlaca";
let actualizarButtonIsActive = false;

const contenedor = document.getElementById("contenedor");
const tbody = document.getElementById("tableBody");

const seriePlacaError = document.getElementById("seriePlacaError");
const estadoError = document.getElementById("estadoError");
const costoError = document.getElementById("costoError");
const comisionError = document.getElementById("comisionError");
const anoRenovacionError = document.getElementById("anoRenovacionError");
const idTipoEstatusError = document.getElementById("idTipoEstatusError");
const idVehiculoError = document.getElementById("idVehiculoError");

const idPlaca = document.getElementById("idPlaca");
const seriePlaca = document.getElementById("seriePlaca");
const estado = document.getElementById("estado");
const costo = document.getElementById("costo");
const comision = document.getElementById("comision");
const anoRenovacion = document.getElementById("anoRenovacion");
const idVehiculo = document.getElementById("idVehiculo");
const idTipoEstatus = document.getElementById("idTipoEstatus");
const idVehiculoSelect = document.getElementById("idVehiculoSelect");
// *********************Execution at start
window.addEventListener("pageshow", function (event) {
  if (event.persisted) {
    window.location.reload();
  }
});

validateLogin();
getAllPlacas();
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
        seriePlaca: seriePlaca.value,
        estado: estado.value,
        costo: costo.value,
        comision: comision.value,
        anoRenovacion: anoRenovacion.value,
        idTipoEstatus: idTipoEstatus.value,
        idVehiculo: idVehiculoSelect.value,
      });

      registerPlaca(raw);
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
          idPlaca: idPlaca.value,
          seriePlaca: seriePlaca.value,
          estado: estado.value,
          costo: costo.value,
          comision: comision.value,
          anoRenovacion: anoRenovacion.value,
          idTipoEstatus: idTipoEstatus.value,
          idVehiculo: idVehiculoSelect.value,
        });
        updatePlaca(raw);
      }
    } else {
      Swal.fire({
        title: "Operación inválida",
        text: "Elige primero un placa para editar",
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
//fechaInicioElement.addEventListener("change", dateValidation);
//fechaTerminoElement.addEventListener("change", dateValidation);
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
  seriePlacaError.textContent = "";
  seriePlaca.classList.remove("borde-rojo");

  estadoError.textContent = "";
  estado.classList.remove("borde-rojo");

  costoError.textContent = "";
  costo.classList.remove("borde-rojo");

  comisionError.textContent = "";
  comision.classList.remove("borde-rojo");

  anoRenovacionError.textContent = "";
  anoRenovacion.classList.remove("borde-rojo");
}

function validateNull() {
  let flag = false;
  if (!seriePlaca.value || seriePlaca.value.trim() === "") {
    seriePlacaError.textContent = "Número de poliza no puede ser nulo";
    seriePlaca.classList.add("borde-rojo");
    flag = true;
  }
  if (!estado.value || estado.value.trim() === "") {
    estadoError.textContent = "Estado no puede ser nulo";
    estado.classList.add("borde-rojo");
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
  if (!anoRenovacion.value || anoRenovacion.value.trim() === "") {
    anoRenovacionError.textContent = "Año renovacion no puede ser nulo";
    anoRenovacion.classList.add("borde-rojo");
    flag = true;
  }
  return flag;
}

function clearForm() {
  seriePlaca.value = "";
  estado.value = "";
  costo.value = "";
  comision.value = "";
  anoRenovacion.value = "";
  idTipoEstatus.value = "1";
  if (idVehiculoSelect.options.length > 0) {
    idVehiculoSelect.selectedIndex = 0;
  }
  idPlaca.value = "";
  actualizarButtonIsActive = false;
}

function setErrorMsgs(result) {
  if (result["seriePlaca"] && result.seriePlaca != "success") {
    seriePlacaError.textContent = result.seriePlaca;
  }
  if (result["Estado"] && result.Estado != "success") {
    estadoError.textContent = result.Estado;
  }

  if (result["Anoderenovacion"] && result.Anoderenovacion != "success") {
    anoRenovacionError.textContent = result.Anoderenovacion;
  }
  if (result["Comision"] && result.Comision != "success") {
    comisionError.textContent = result.Comision;
  }
  if (result["Costo"] && result.Costo != "success") {
    costoError.textContent = result.Costo;
  }
  if (result["IdTipoEstatus"] && result.IdTipoEstatus != "success") {
    idTipoEstatusError.textContent = result.IdTipoEstatus;
  }
  if (result["IdVehiculo"] && result.IdVehiculo != "success") {
    idVehiculoError.textContent = result.IdVehiculo;
  }
}

function editePlaca(placaString) {
  clearAll();
  const placa = JSON.parse(placaString);
  idPlaca.value = placa.idPlaca;
  seriePlaca.value = placa.seriePlaca;
  estado.value = placa.estado;
  costo.value = placa.costo;
  comision.value = placa.comision;
  anoRenovacion.value = placa.anoRenovacion;
  if (placa.estatusPlaca == "activo") {
    idTipoEstatus.value = 1;
  } else {
    idTipoEstatus.value = 2;
  }
  const option = document.createElement("option");
  option.value = placa.idVehiculo;
  option.textContent = `${placa.numeroSerie}`;
  idVehiculoSelect.appendChild(option);
  idVehiculoSelect.value = placa.idVehiculo;

  actualizarButtonIsActive = true;
}

function createTable(placas) {
  tbody.innerHTML = "";
  placas.forEach((placa) => {
    const row = document.createElement("tr");
    const placaString = JSON.stringify(placa).replace(/"/g, "&quot;");
    row.innerHTML = `
          <td>${placa.idPlaca}</td>
          <td>${placa.seriePlaca}</td>
          <td>${placa.estado}</td>
          <td>${placa.costo}</td>
          <td>${placa.comision}</td>
          <td>${placa.total}</td>
          <td>${placa.totalConIva}</td>
          <td>${placa.anoRenovacion}</td>
          <td>${placa.estatusPlaca}</td>
          <td>${placa.numeroSerie}</td>
          <td>${placa.estatusVehiculo}</td>
                ${
                  window.asesorMode
                    ? ""
                    : `<td><button class="edit-btn" onclick="editePlaca('${placaString}')">Editar</button></td>
                       <td><button class="delete-btn" onclick="deletePlaca('${placa.seriePlaca}')">Eliminar</button></td>`
                }
              `;

    tbody.appendChild(row);
  });
}

function showActivePlacas() {
  const activos = placasData.filter((u) => u.estatusPlaca === "activo");
  createTable(activos);
}

function showInactivePlacas() {
  const noActivos = placasData.filter((u) => u.estatusPlaca === "inactivo");
  createTable(noActivos);
}

function showAllPlacas() {
  createTable(placasData);
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
          const placaForm = document.getElementById("placaForm");
          if (placaForm) placaForm.remove();
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

function getAllPlacas() {
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
        placasData = result.myArrayList.map((item) => item.map);
        createTable(placasData);
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

function registerPlaca(raw) {
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
        getAllPlacas();
        getAllVehiculos();
        clearAll();
      }
    })
    .catch((error) => {
      let errorMsg = error;
    });
}

function deletePlaca(seriePlaca) {
  Swal.fire({
    title: "¿Quieres eliminar este placa?",
    text: "Esta acción no podrá revertirse.",
    icon: "warning",
    showCancelButton: true,
    confirmButtonColor: "#3085d6",
    cancelButtonColor: "#d33",
    confirmButtonText: "Eliminar definitivamente",
    cancelButtonText: `Cancelar`,
  }).then((result) => {
    if (result.isConfirmed) {
      const raw = JSON.stringify({ seriePlaca: seriePlaca });

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
            getAllPlacas();
            getAllVehiculos();
          }
        })
        .catch((error) => {
          let errorMsg = error;
        });
    }
  });
}

function updatePlaca(raw) {
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
        getAllPlacas();
        getAllVehiculos();
        clearAll();
        actualizarButtonIsActive = false;
      }
    })
    .catch((error) => {
      let errorMsg = error;
    });
}
