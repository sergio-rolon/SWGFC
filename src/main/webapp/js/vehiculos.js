let vehiculosData = [];
let rowsFiltered = [];
let urlLogged = "/api/usuarios/logged";
let url = "/api/vehiculos";
let urlClientes = "/api/clientes/getAllClientes";
let actualizarButtonIsActive = false;
let currentPage = 1;
const rowsPerPage = 5;
const contenedor = document.getElementById("contenedor");
const tbody = document.getElementById("tableBody");

const numeroSerieError = document.getElementById("numeroSerieError");
const marcaError = document.getElementById("marcaError");
const tipoError = document.getElementById("tipoError");
const modeloError = document.getElementById("modeloError");
const accesoriosError = document.getElementById("accesoriosError");
const idTipoEstatusError = document.getElementById("idTipoEstatusError");
const idClienteError = document.getElementById("idClienteError");

const idVehiculo = document.getElementById("idVehiculo");
const numeroSerie = document.getElementById("numeroSerie");
const marca = document.getElementById("marca");
const tipo = document.getElementById("tipo");
const modelo = document.getElementById("modelo");
const accesorios = document.getElementById("accesorios");
const idCliente = document.getElementById("idCliente");
const idTipoEstatus = document.getElementById("idTipoEstatus");
const idClienteSelect = document.getElementById("idClienteSelect");
// *********************Execution at start
window.addEventListener("pageshow", function (event) {
  if (event.persisted) {
    window.location.reload();
  }
});

validateLogin();
getAllVehiculos();
getAllClientes();

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
        numeroSerie: numeroSerie.value,
        marca: marca.value,
        tipo: tipo.value,
        modelo: modelo.value,
        accesorios: accesorios.value,
        idTipoEstatus: idTipoEstatus.value,
        idCliente: idClienteSelect.value,
      });

      registerVehiculo(raw);
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
          idVehiculo: idVehiculo.value,
          numeroSerie: numeroSerie.value,
          marca: marca.value,
          tipo: tipo.value,
          modelo: modelo.value,
          accesorios: accesorios.value,
          idTipoEstatus: idTipoEstatus.value,
          idCliente: idClienteSelect.value,
        });
        updateVehiculo(raw);
      }
    } else {
      Swal.fire({
        title: "Operación inválida",
        text: "Elige primero un vehículo para editar",
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
    "Id Vehículo",
    "Número de serie",
    "Marca",
    "Tipo",
    "Modelo",
    "Accesorios",
    "Estatus vehículo",
    "RFC",
    "Razón social",
    "Estatus cliente",
  ];

  const rows = vehiculosData.map((vehiculo) => [
    vehiculo.idVehiculo,
    vehiculo.numeroSerie,
    vehiculo.marca,
    vehiculo.tipo,
    vehiculo.modelo,
    vehiculo.accesorios,
    vehiculo.estatusVehiculo,
    vehiculo.rfc,
    vehiculo.razonSocial,
    vehiculo.estatusCliente,
  ]);

  const workSheet = XLSX.utils.aoa_to_sheet([headers, ...rows]);

  const workBook = XLSX.utils.book_new();
  XLSX.utils.book_append_sheet(workBook, workSheet, "Vehículos");

  XLSX.writeFile(workBook, "vehiculos_reporte.xlsx");
}

function populateSecondDropdown() {
  const secondDropdown = document.getElementById("secondDropdown");
  secondDropdown.innerHTML = "";

  const uniqueRazonSocial = [
    ...new Set(vehiculosData.map((v) => `${v.razonSocial}`)),
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

  const filteredSelection = vehiculosData.filter(
    (vehiculo) =>
      selectedEstatus.includes(vehiculo.estatusCliente) &&
      selectedRazonSocial.includes(vehiculo.razonSocial)
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
  numeroSerieError.textContent = "";
  numeroSerie.classList.remove("borde-rojo");

  marcaError.textContent = "";
  marca.classList.remove("borde-rojo");

  tipoError.textContent = "";
  tipo.classList.remove("borde-rojo");

  modeloError.textContent = "";
  modelo.classList.remove("borde-rojo");

  accesoriosError.textContent = "";
  accesorios.classList.remove("borde-rojo");
}

function validateNull() {
  let flag = false;
  if (!numeroSerie.value || numeroSerie.value.trim() === "") {
    numeroSerieError.textContent = "Número de serie no puede ser nulo";
    numeroSerie.classList.add("borde-rojo");
    flag = true;
  }
  if (!marca.value || marca.value.trim() === "") {
    marcaError.textContent = "Marca no puede ser nulo";
    marca.classList.add("borde-rojo");
    flag = true;
  }
  if (!tipo.value || tipo.value.trim() === "") {
    tipoError.textContent = "Tipo no puede ser nulo";
    tipo.classList.add("borde-rojo");
    flag = true;
  }
  if (!modelo.value || modelo.value.trim() === "") {
    modeloError.textContent = "Modelo no puede ser nulo";
    modelo.classList.add("borde-rojo");
    flag = true;
  }
  if (!accesorios.value || accesorios.value.trim() === "") {
    accesoriosError.textContent = "Accesorios no puede ser nulo";
    accesorios.classList.add("borde-rojo");
    flag = true;
  }
  return flag;
}

function clearForm() {
  numeroSerie.value = "";
  marca.value = "";
  tipo.value = "";
  modelo.value = "";
  accesorios.value = "";
  idTipoEstatus.value = "1";
  if (idClienteSelect.options.length > 0) {
    idClienteSelect.selectedIndex = 0;
  }
  idVehiculo.value = "";
  actualizarButtonIsActive = false;
}

function setErrorMsgs(result) {
  if (result["numeroSerie"] && result.numeroSerie != "success") {
    numeroSerieError.textContent = result.numeroSerie;
  }
  if (result["Marca"] && result.Marca != "success") {
    marcaError.textContent = result.Marca;
  }
  if (result["Tipo"] && result.Tipo != "success") {
    tipoError.textContent = result.Tipo;
  }
  if (result["Modelo"] && result.Modelo != "success") {
    modeloError.textContent = result.Modelo;
  }
  if (result["Accesorios"] && result.Accesorios != "success") {
    accesoriosError.textContent = result.Accesorios;
  }
  if (result["IdTipoEstatus"] && result.IdTipoEstatus != "success") {
    idTipoEstatusError.textContent = result.IdTipoEstatus;
  }
  if (result["IdCliente"] && result.IdCliente != "success") {
    idClienteError.textContent = result.IdCliente;
  }
}

function editeVehiculo(vehiculoString) {
  const elementTop =
    document.getElementById("main").getBoundingClientRect().top +
    window.scrollY;
  window.scrollTo({
    top: elementTop - 46,
    behavior: "smooth",
  });
  clearAll();
  const vehiculo = JSON.parse(vehiculoString);
  idVehiculo.value = vehiculo.idVehiculo;
  numeroSerie.value = vehiculo.numeroSerie;
  marca.value = vehiculo.marca;
  tipo.value = vehiculo.tipo;
  modelo.value = vehiculo.modelo;
  accesorios.value = vehiculo.accesorios;
  if (vehiculo.estatusVehiculo == "Activo") {
    idTipoEstatus.value = 1;
  } else {
    idTipoEstatus.value = 2;
  }
  idClienteSelect.value = vehiculo.idCliente;

  actualizarButtonIsActive = true;
}

function createTable(vehiculos, page = 1) {
  tbody.innerHTML = "";

  const startIndex = (page - 1) * rowsPerPage;
  const endIndex = startIndex + rowsPerPage;
  const paginatedVehiculos = vehiculos.slice(startIndex, endIndex);

  paginatedVehiculos.forEach((vehiculo) => {
    const row = document.createElement("tr");
    const vehiculoString = JSON.stringify(vehiculo).replace(/"/g, "&quot;");
    row.innerHTML = `
          <td>${vehiculo.idVehiculo}</td>
          <td>${vehiculo.numeroSerie}</td>
          <td>${vehiculo.marca}</td>
          <td>${vehiculo.tipo}</td>
          <td>${vehiculo.modelo}</td>
          <td>${vehiculo.accesorios}</td>
          <td>${vehiculo.estatusVehiculo}</td>
          <td>${vehiculo.rfc}</td>
          <td>${vehiculo.razonSocial}</td>
          <td>${vehiculo.estatusCliente}</td>
                ${
                  window.asesorMode
                    ? ""
                    : `<td><button class="edit-btn" onclick="editeVehiculo('${vehiculoString}')">Editar</button></td>
                       <td><button class="delete-btn" onclick="deleteVehiculo('${vehiculo.numeroSerie}')">Eliminar</button></td>`
                }
              `;

    tbody.appendChild(row);
  });

  renderPagination(vehiculos, page);
}

function renderPagination(vehiculos, page) {
  const paginationContainer = document.getElementById("paginationDiv");
  paginationContainer.innerHTML = "";

  const pageCount = Math.ceil(vehiculos.length / rowsPerPage);

  if (page > 1) {
    const prevButton = document.createElement("button");
    prevButton.textContent = "Anterior";
    prevButton.addEventListener("click", () => {
      createTable(vehiculos, page - 1);
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
      createTable(vehiculos, i);
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

function showActiveVehiculos() {
  const activos = vehiculosData.filter((u) => u.estatusVehiculo === "Activo");
  createTable(activos);
}

function showInactiveVehiculos() {
  const noActivos = vehiculosData.filter(
    (u) => u.estatusVehiculo === "Inactivo"
  );
  createTable(noActivos);
}

function showAllVehiculos() {
  createTable(vehiculosData);
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
        if (usuario.role === "Operación") {
          document.getElementById("emailUserLogged").textContent =
            usuario.email;
          document.getElementById("loader").style.display = "none";
          document.getElementById("contenido").style.visibility = "visible";
        } else if (usuario.role === "Asesor") {
          const vehiculoForm = document.getElementById("vehiculoForm");
          if (vehiculoForm) vehiculoForm.remove();
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
        vehiculosData = result.myArrayList.map((item) => item.map);
        rowsFiltered = [...vehiculosData];
        createTable(vehiculosData);
        populateSecondDropdown();
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
        idClienteSelect.innerHTML = "";
        let clientes = result.myArrayList.map((item) => item.map);
        clientes.forEach((cliente) => {
          const option = document.createElement("option");
          option.value = cliente.idCliente;
          option.textContent = `${cliente.razonSocial} - ${cliente.rfc}`;
          idClienteSelect.appendChild(option);
        });
      }
    })
    .catch((error) => {
      let errorMsg = error;
    });
}

function registerVehiculo(raw) {
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
        getAllVehiculos();
        clearAll();
      }
    })
    .catch((error) => {
      let errorMsg = error;
    });
}

function deleteVehiculo(numeroSerie) {
  Swal.fire({
    title: "¿Quieres eliminar este vehículo?",
    text: "Esta acción no podrá revertirse.",
    icon: "warning",
    showCancelButton: true,
    confirmButtonColor: "#3085d6",
    cancelButtonColor: "#d33",
    confirmButtonText: "Eliminar definitivamente",
    cancelButtonText: `Cancelar`,
  }).then((result) => {
    if (result.isConfirmed) {
      const raw = JSON.stringify({ numeroSerie: numeroSerie });

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
            getAllVehiculos();
          }
        })
        .catch((error) => {
          let errorMsg = error;
        });
    }
  });
}

function updateVehiculo(raw) {
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

  rowsFiltered = vehiculosData.filter((vehiculo) =>
    vehiculo.numeroSerie.toLowerCase().includes(searchedValue)
  );

  createTable(rowsFiltered, 1);
});
