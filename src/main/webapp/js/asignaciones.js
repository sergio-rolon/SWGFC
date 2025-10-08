let asignacionesData = [];
let rowsFiltered = [];
let clientesData = [];
let empleadosData = [];
let vehiculosData = [];
let urlLogged = "/api/usuarios/logged";
let url = "/api/asignaciones";
let urlVehiculos = "/api/vehiculos/getVehiculosParaAsignacion";
let urlClientes = "/api/clientes/getAllClientes";
let urlEmpleados = "/api/empleados/getEmpleadosParaAsignacion";
let actualizarButtonIsActive = false;
let currentPage = 1;
const rowsPerPage = 5;
const contenedor = document.getElementById("contenedor");
const tbody = document.getElementById("tableBody");

const idEmpleadoError = document.getElementById("idEmpleadoError");
const idTipoEstatusError = document.getElementById("idTipoEstatusError");
const idVehiculoError = document.getElementById("idVehiculoError");
const idClienteError = document.getElementById("idClienteError");
const idAsignacion = document.getElementById("idAsignacion");
const idEmpleado = document.getElementById("idEmpleado");
const idVehiculo = document.getElementById("idVehiculo");
const idTipoEstatus = document.getElementById("idTipoEstatus");
const idVehiculoSelect = document.getElementById("idVehiculoSelect");
const idEmpleadoSelect = document.getElementById("idEmpleadoSelect");
const idClientesSelect = document.getElementById("idClientesSelect");
// *********************Execution at start
window.addEventListener("pageshow", function (event) {
  if (event.persisted) {
    window.location.reload();
  }
});

validateLogin();
getAllAsignaciones();
getAllClientes();
getAllVehiculos();
getAllEmpleados();
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
        idEmpleado: idEmpleadoSelect.value,
        idCliente: idClientesSelect.value,
        idVehiculo: idVehiculoSelect.value,
        idTipoEstatus: idTipoEstatus.value,
      });

      registerAsignacion(raw);
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
          idAsignacion: idAsignacion.value,
          idEmpleado: idEmpleadoSelect.value,
          idCliente: idClientesSelect.value,
          idVehiculo: idVehiculoSelect.value,
          idTipoEstatus: idTipoEstatus.value,
        });
        updateAsignacion(raw);
      }
    } else {
      Swal.fire({
        title: "Operación inválida",
        text: "Elige primero una asignación para editar",
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
    "Id Asignación",
    "Estatus asignación",
    "Número de serie",
    "Marca",
    "Tipo",
    "Modelo",
    "Número de trabajador",
    "Nombre",
    "Apellido paterno",
    "Apellido materno",
    "Razón social",
  ];

  const rows = asignacionesData.map((asignacion) => [
    asignacion.idAsignacion,
    asignacion.estatusAsignacion,
    asignacion.numeroSerie,
    asignacion.marca,
    asignacion.tipo,
    asignacion.modelo,
    asignacion.numeroTrabajador,
    asignacion.nombre,
    asignacion.apellidoPaterno,
    asignacion.apellidoMaterno,
    asignacion.razonSocial,
  ]);

  const workSheet = XLSX.utils.aoa_to_sheet([headers, ...rows]);

  const workBook = XLSX.utils.book_new();
  XLSX.utils.book_append_sheet(workBook, workSheet, "Asignaciones");

  XLSX.writeFile(workBook, "asignaciones_reporte.xlsx");
}

function populateSecondDropdown() {
  const secondDropdown = document.getElementById("secondDropdown");
  secondDropdown.innerHTML = "";

  const uniqueRazonSocial = [
    ...new Set(asignacionesData.map((a) => `${a.razonSocial}`)),
  ];

  uniqueRazonSocial.forEach((razonSocial) => {
    const label = document.createElement("label");
    label.innerHTML = `
      <input type="checkbox" class="second-filter" value="${razonSocial}" checked /> ${razonSocial}
    `;
    secondDropdown.appendChild(label);
    secondDropdown.appendChild(document.createElement("br"));
  });
}

function filterSelection() {
  const selectedEstatus = Array.from(
    document.querySelectorAll(".estatus-filter:checked")
  ).map((cb) => cb.value);
  const selectedRazonSocial = Array.from(
    document.querySelectorAll(".second-filter:checked")
  ).map((cb) => cb.value);

  const filteredSelection = asignacionesData.filter(
    (asignacion) =>
      selectedEstatus.includes(asignacion.estatusAsignacion) &&
      selectedRazonSocial.includes(asignacion.razonSocial)
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

function clearAll() {
  clearErrors();
  clearForm();
}

function clearErrors() {
  idClienteError.textContent = "";
  idClientesSelect.classList.remove("borde-rojo");

  idEmpleadoError.textContent = "";
  idEmpleadoSelect.classList.remove("borde-rojo");

  idVehiculoError.textContent = "";
  idVehiculoSelect.classList.remove("borde-rojo");

  idTipoEstatusError.textContent = "";
  idTipoEstatus.classList.remove("borde-rojo");
}
function clearForm() {
  idAsignacion.value = "";
  idEmpleadoSelect.innerHTML = "";
  idVehiculoSelect.innerHTML = "";
  idClientesSelect.selectedIndex = 0;
}
function validateNull() {
  let flag = false;
  if (!idClientesSelect.value || idClientesSelect.value.trim() === "") {
    idClienteError.textContent = "Cliente no puede ser nulo";
    idClientesSelect.classList.add("borde-rojo");
    flag = true;
  }
  if (!idEmpleadoSelect.value || idEmpleadoSelect.value.trim() === "") {
    idEmpleadoError.textContent = "Empleado no puede ser nulo";
    idEmpleadoSelect.classList.add("borde-rojo");
    flag = true;
  }
  if (!idVehiculoSelect.value || idVehiculoSelect.value.trim() === "") {
    idVehiculoError.textContent = "Vehículo no puede ser nulo";
    idVehiculoSelect.classList.add("borde-rojo");
    flag = true;
  }
  if (!idTipoEstatus.value || idTipoEstatus.value.trim() === "") {
    idTipoEstatusError.textContent = "Tipo estatus no puede ser nulo";
    idTipoEstatus.classList.add("borde-rojo");
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
    idEmpleadoSelect.innerHTML = "";
    idVehiculoSelect.innerHTML = "";
    return;
  }

  // Filtra empleados y vehículos relacionados
  const empleadosFiltrados = empleadosData.filter(
    (e) => e.idCliente == clienteSeleccionado
  );
  const vehiculosFiltrados = vehiculosData.filter(
    (v) => v.idCliente == clienteSeleccionado
  );

  // Llena los selects
  fillSelect(
    idEmpleadoSelect,
    empleadosFiltrados,
    "idEmpleado",
    (empleado) =>
      `${empleado.numeroTrabajador} - ${empleado.nombre} ${empleado.apellidoPaterno} ${empleado.apellidoMaterno}`
  );
  fillSelect(
    idVehiculoSelect,
    vehiculosFiltrados,
    "idVehiculo",
    (vehiculo) => vehiculo.numeroSerie
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

function setErrorMsgs(result) {
  if (result["IdEmpleado"] && result.IdEmpleado != "success") {
    idEmpleadoError.textContent = result.IdEmpleado;
  }
  if (result["IdTipoEstatus"] && result.IdTipoEstatus != "success") {
    idTipoEstatusError.textContent = result.IdTipoEstatus;
  }
  if (result["IdVehiculo"] && result.IdVehiculo != "success") {
    idVehiculoError.textContent = result.IdVehiculo;
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

function editeAsignacion(asignacionString) {
  const elementTop =
    document.getElementById("main").getBoundingClientRect().top +
    window.scrollY;
  window.scrollTo({
    top: elementTop - 46,
    behavior: "smooth",
  });
  clearAll();
  const asignacion = JSON.parse(asignacionString);
  idAsignacion.value = asignacion.idAsignacion;
  idTipoEstatus.value = asignacion.estatusAsignacion === "Activo" ? 1 : 2;
  idClientesSelect.value = asignacion.idCliente;

  const clienteSeleccionado = idClientesSelect.value;

  if (!clienteSeleccionado) {
    idEmpleadoSelect.innerHTML = "";
    idVehiculoSelect.innerHTML = "";
    return;
  }

  // Filtra empleados y vehículos relacionados
  const empleadosFiltrados = empleadosData.filter(
    (e) => e.idCliente == clienteSeleccionado
  );
  const vehiculosFiltrados = vehiculosData.filter(
    (v) => v.idCliente == clienteSeleccionado
  );

  // Llena los selects
  fillSelect(
    idEmpleadoSelect,
    empleadosFiltrados,
    "idEmpleado",
    (asignacion) =>
      `${asignacion.numeroTrabajador} - ${asignacion.nombre} ${asignacion.apellidoPaterno} ${asignacion.apellidoMaterno}`
  );
  fillSelect(
    idVehiculoSelect,
    vehiculosFiltrados,
    "idVehiculo",
    (vehiculo) => vehiculo.numeroSerie
  );
  idEmpleadoSelect.value = asignacion.idEmpleado;
  idVehiculoSelect.value = asignacion.idVehiculo;

  actualizarButtonIsActive = true;
}

function createTable(asignaciones, page = 1) {
  tbody.innerHTML = "";

  const startIndex = (page - 1) * rowsPerPage;
  const endIndex = startIndex + rowsPerPage;
  const paginatedAsignaciones = asignaciones.slice(startIndex, endIndex);

  paginatedAsignaciones.forEach((asignacion) => {
    const row = document.createElement("tr");
    const asignacionString = JSON.stringify(asignacion).replace(/"/g, "&quot;");
    row.innerHTML = `
          <td>${asignacion.idAsignacion}</td>
          <td>${asignacion.estatusAsignacion}</td>
          <td>${asignacion.numeroSerie}</td>
          <td>${asignacion.marca}</td>  
          <td>${asignacion.tipo}</td>  
          <td>${asignacion.modelo}</td>  
          <td>${asignacion.numeroTrabajador}</td>
          <td>${asignacion.nombre}</td>  
          <td>${asignacion.apellidoPaterno}</td>  
          <td>${asignacion.apellidoMaterno}</td>  
          <td>${asignacion.razonSocial}</td>
                ${
                  window.asesorMode
                    ? ""
                    : `<td><button class="edit-btn" onclick="editeAsignacion('${asignacionString}')">Editar</button></td>
                       <td><button class="delete-btn" onclick="deleteAsignacion('${asignacion.idAsignacion}')">Eliminar</button></td>`
                }
              `;

    tbody.appendChild(row);
  });

  renderPagination(asignaciones, page);
}

function renderPagination(asignaciones, page) {
  const paginationContainer = document.getElementById("paginationDiv");
  paginationContainer.innerHTML = "";

  const pageCount = Math.ceil(asignaciones.length / rowsPerPage);

  if (page > 1) {
    const prevButton = document.createElement("button");
    prevButton.textContent = "Anterior";
    prevButton.addEventListener("click", () => {
      createTable(asignaciones, page - 1);
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
      createTable(asignaciones, i);
    });
    paginationContainer.appendChild(pageButton);
  }

  if (page < pageCount) {
    const nextButton = document.createElement("button");
    nextButton.textContent = "Siguiente";
    nextButton.addEventListener("click", () => {
      createTable(vehiculos, page + 1);
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
        if (usuario.role === "Operación") {
          document.getElementById("emailUserLogged").textContent =
            usuario.email;
          document.getElementById("loader").style.display = "none";
          document.getElementById("contenido").style.visibility = "visible";
        } else if (usuario.role === "Asesor") {
          const asignacionForm = document.getElementById("asignacionForm");
          if (asignacionForm) asignacionForm.remove();
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
        asignacionesData = result.myArrayList.map((item) => item.map);
        rowsFiltered = [...asignacionesData];
        createTable(asignacionesData);
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
          '<option value="0">-- Seleccione --</option>';
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

function getAllEmpleados() {
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

  fetch(urlEmpleados, requestOptions)
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
        empleadosData = result.myArrayList.map((item) => item.map);
      }
    })
    .catch((error) => {
      let errorMsg = error;
    });
}

function registerAsignacion(raw) {
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
        getAllAsignaciones();
        getAllClientes();
        getAllVehiculos();
        getAllEmpleados();
        clearAll();
      }
    })
    .catch((error) => {
      let errorMsg = error;
    });
}
function showAllAsignaciones() {
  const checkboxes1 = document.querySelectorAll(".estatus-filter");
  checkboxes1.forEach((chk) => (chk.checked = true));

  const checkboxes2 = document.querySelectorAll(".second-filter");
  checkboxes2.forEach((chk) => (chk.checked = true));

  createTable(asignacionesData);
  populateSecondDropdown();
}
function deleteAsignacion(idAsignacion) {
  Swal.fire({
    title: "¿Quieres eliminar este asignacion?",
    text: "Esta acción no podrá revertirse.",
    icon: "warning",
    showCancelButton: true,
    confirmButtonColor: "#3085d6",
    cancelButtonColor: "#d33",
    confirmButtonText: "Eliminar definitivamente",
    cancelButtonText: `Cancelar`,
  }).then((result) => {
    if (result.isConfirmed) {
      const raw = JSON.stringify({ idAsignacion: idAsignacion });

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

            getAllAsignaciones();
            getAllClientes();
            getAllVehiculos();
            getAllEmpleados();
            clearAll();
          }
        })
        .catch((error) => {
          let errorMsg = error;
        });
    }
  });
}

function updateAsignacion(raw) {
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
        getAllAsignaciones();
        getAllClientes();
        getAllVehiculos();
        getAllEmpleados();
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

  rowsFiltered = asignacionesData.filter((asignacion) =>
    asignacion.numeroSerie.toLowerCase().includes(searchedValue)
  );

  createTable(rowsFiltered, 1);
});
