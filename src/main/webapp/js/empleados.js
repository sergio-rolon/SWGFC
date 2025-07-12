let empleadosData = [];
let urlLogged = "/api/usuarios/logged";
let url = "/api/empleados";
let urlClientes = "/api/clientes/getAllClientes";
let actualizarButtonIsActive = false;
let currentPage = 1;
const rowsPerPage = 5;
const contenedor = document.getElementById("contenedor");
const tbody = document.getElementById("tableBody");

const numeroTrabajadorError = document.getElementById("numeroTrabajadorError");
const nombreError = document.getElementById("nombreError");
const apellidoPaternoError = document.getElementById("apellidoPaternoError");
const apellidoMaternoError = document.getElementById("apellidoMaternoError");
const municipioAsignadoError = document.getElementById(
  "municipioAsignadoError"
);
const estadoAsignadoError = document.getElementById("estadoAsignadoError");
const cantidadGasolinaError = document.getElementById("cantidadGasolinaError");
const idTipoEstatusError = document.getElementById("idTipoEstatusError");
const idClienteError = document.getElementById("idClienteError");

const idEmpleado = document.getElementById("idEmpleado");
const numeroTrabajador = document.getElementById("numeroTrabajador");
const nombre = document.getElementById("nombre");
const apellidoPaterno = document.getElementById("apellidoPaterno");
const apellidoMaterno = document.getElementById("apellidoMaterno");
const municipioAsignado = document.getElementById("municipioAsignado");
const estadoAsignado = document.getElementById("estadoAsignado");
const cantidadGasolina = document.getElementById("cantidadGasolina");
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
getAllEmpleados();
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
        numeroTrabajador: numeroTrabajador.value,
        nombre: nombre.value,
        apellidoPaterno: apellidoPaterno.value,
        apellidoMaterno: apellidoMaterno.value,
        municipioAsignado: municipioAsignado.value,
        estadoAsignado: estadoAsignado.value,
        cantidadGasolina: cantidadGasolina.value,
        idTipoEstatus: idTipoEstatus.value,
        idCliente: idClienteSelect.value,
      });

      registerEmpleado(raw);
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
          idEmpleado: idEmpleado.value,
          numeroTrabajador: numeroTrabajador.value,
          nombre: nombre.value,
          apellidoPaterno: apellidoPaterno.value,
          apellidoMaterno: apellidoMaterno.value,
          municipioAsignado: municipioAsignado.value,
          estadoAsignado: estadoAsignado.value,
          cantidadGasolina: cantidadGasolina.value,
          idTipoEstatus: idTipoEstatus.value,
          idCliente: idClienteSelect.value,
        });
        updateEmpleado(raw);
      }
    } else {
      Swal.fire({
        title: "Operación inválida",
        text: "Elige primero un empleado para editar",
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
    "Id empleado",
    "Número de trabajador",
    "Nombre",
    "Apellido paterno",
    "Apellido materno",
    "Municipio asignado",
    "Estado asignado",
    "Cantidad gasolina",
    "Estatus empleado",
    "RFC",
    "Razón social",
    "Estatus cliente",
  ];

  const rows = empleadosData.map((empleado) => [
    empleado.idEmpleado,
    empleado.numeroTrabajador,
    empleado.nombre,
    empleado.apellidoPaterno,
    empleado.apellidoMaterno,
    empleado.municipioAsignado,
    empleado.estadoAsignado,
    empleado.cantidadGasolina,
    empleado.estatusEmpleado,
    empleado.rfc,
    empleado.razonSocial,
    empleado.estatusCliente,
  ]);

  const workSheet = XLSX.utils.aoa_to_sheet([headers, ...rows]);

  const workBook = XLSX.utils.book_new();
  XLSX.utils.book_append_sheet(workBook, workSheet, "Empleados");

  XLSX.writeFile(workBook, "empleados_reporte.xlsx");
}

function populateSecondDropdown() {
  const secondDropdown = document.getElementById("secondDropdown");
  secondDropdown.innerHTML = "";

  const uniqueRazonSocial = [
    ...new Set(empleadosData.map((v) => `${v.razonSocial}`)),
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

  const filteredSelection = empleadosData.filter(
    (empleado) =>
      selectedEstatus.includes(empleado.estatusCliente) &&
      selectedRazonSocial.includes(empleado.razonSocial)
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
  numeroTrabajadorError.textContent = "";
  numeroTrabajador.classList.remove("borde-rojo");

  nombreError.textContent = "";
  nombre.classList.remove("borde-rojo");

  apellidoPaternoError.textContent = "";
  apellidoPaterno.classList.remove("borde-rojo");

  apellidoMaternoError.textContent = "";
  apellidoMaterno.classList.remove("borde-rojo");

  municipioAsignadoError.textContent = "";
  municipioAsignado.classList.remove("borde-rojo");

  estadoAsignadoError.textContent = "";
  estadoAsignado.classList.remove("borde-rojo");

  cantidadGasolinaError.textContent = "";
  cantidadGasolina.classList.remove("borde-rojo");
}

function validateNull() {
  let flag = false;
  if (!numeroTrabajador.value || numeroTrabajador.value.trim() === "") {
    numeroTrabajadorError.textContent =
      "Número de trabajador no puede ser nulo";
    numeroTrabajador.classList.add("borde-rojo");
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
    apellidoMaternoError.textContent = "Apellido materno no puede ser nulo";
    apellidoMaterno.classList.add("borde-rojo");
    flag = true;
  }
  if (!municipioAsignado.value || municipioAsignado.value.trim() === "") {
    municipioAsignadoError.textContent = "Municipio no puede ser nulo";
    municipioAsignado.classList.add("borde-rojo");
    flag = true;
  }
  if (!estadoAsignado.value || estadoAsignado.value.trim() === "") {
    estadoAsignadoError.textContent = "Estado no puede ser nulo";
    estadoAsignado.classList.add("borde-rojo");
    flag = true;
  }
  if (!cantidadGasolina.value || cantidadGasolina.value.trim() === "") {
    cantidadGasolinaError.textContent = "Cantidad gasolina no puede ser nulo";
    cantidadGasolina.classList.add("borde-rojo");
    flag = true;
  }
  return flag;
}

function clearForm() {
  numeroTrabajador.value = "";
  nombre.value = "";
  apellidoPaterno.value = "";
  apellidoMaterno.value = "";
  municipioAsignado.value = "";
  estadoAsignado.value = "";
  cantidadGasolina.value = "";
  idTipoEstatus.value = "1";
  if (idClienteSelect.options.length > 0) {
    idClienteSelect.selectedIndex = 0;
  }
  idEmpleado.value = "";
  actualizarButtonIsActive = false;
}

function setErrorMsgs(result) {
  if (result["numeroTrabajador"] && result.numeroTrabajador != "success") {
    numeroTrabajadorError.textContent = result.numeroTrabajador;
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
  if (result["Municipioasignado"] && result.Municipioasignado != "success") {
    municipioAsignadoError.textContent = result.Municipioasignado;
  }
  if (result["Estadoasignado"] && result.Estadoasignado != "success") {
    estadoAsignadoError.textContent = result.Estadoasignado;
  }
  if (result["Cantidadgasolina"] && result.Cantidadgasolina != "success") {
    cantidadGasolinaError.textContent = result.Cantidadgasolina;
  }
  if (result["IdTipoEstatus"] && result.IdTipoEstatus != "success") {
    idTipoEstatusError.textContent = result.IdTipoEstatus;
  }
  if (result["IdCliente"] && result.IdCliente != "success") {
    idClienteError.textContent = result.IdCliente;
  }
}

function editeEmpleado(empleadoString) {
  const elementTop =
    document.getElementById("main").getBoundingClientRect().top +
    window.scrollY;
  window.scrollTo({
    top: elementTop - 46,
    behavior: "smooth",
  });
  clearAll();
  const empleado = JSON.parse(empleadoString);
  idEmpleado.value = empleado.idEmpleado;
  numeroTrabajador.value = empleado.numeroTrabajador;
  nombre.value = empleado.nombre;
  apellidoPaterno.value = empleado.apellidoPaterno;
  apellidoMaterno.value = empleado.apellidoMaterno;
  municipioAsignado.value = empleado.municipioAsignado;
  estadoAsignado.value = empleado.estadoAsignado;
  cantidadGasolina.value = empleado.cantidadGasolina;
  if (empleado.estatusEmpleado == "activo") {
    idTipoEstatus.value = 1;
  } else {
    idTipoEstatus.value = 2;
  }
  idClienteSelect.value = empleado.idCliente;

  actualizarButtonIsActive = true;
}

function createTable(empleados, page = 1) {
  tbody.innerHTML = "";

  const startIndex = (page - 1) * rowsPerPage;
  const endIndex = startIndex + rowsPerPage;
  const paginatedEmpleados = empleados.slice(startIndex, endIndex);

  paginatedEmpleados.forEach((empleado) => {
    const row = document.createElement("tr");
    const empleadoString = JSON.stringify(empleado).replace(/"/g, "&quot;");
    row.innerHTML = `
          <td>${empleado.idEmpleado}</td>
          <td>${empleado.numeroTrabajador}</td>
          <td>${empleado.nombre}</td>
          <td>${empleado.apellidoPaterno}</td>
          <td>${empleado.apellidoMaterno}</td>
          <td>${empleado.municipioAsignado}</td>
           <td>${empleado.estadoAsignado}</td>
           <td>${empleado.cantidadGasolina}</td>
          <td>${empleado.estatusEmpleado}</td>
          <td>${empleado.rfc}</td>
          <td>${empleado.razonSocial}</td>
          <td>${empleado.estatusCliente}</td>
                ${
                  window.operacionMode
                    ? ""
                    : `<td><button class="edit-btn" onclick="editeEmpleado('${empleadoString}')">Editar</button></td>
                       <td><button class="delete-btn" onclick="deleteEmpleado('${empleado.numeroTrabajador}')">Eliminar</button></td>`
                }
              `;

    tbody.appendChild(row);
  });

  renderPagination(empleados, page);
}

function renderPagination(empleados, page) {
  const paginationContainer = document.getElementById("paginationDiv");
  paginationContainer.innerHTML = "";

  const pageCount = Math.ceil(empleados.length / rowsPerPage);

  if (page > 1) {
    const prevButton = document.createElement("button");
    prevButton.textContent = "Anterior";
    prevButton.addEventListener("click", () => {
      createTable(empleados, page - 1);
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
      createTable(empleados, i);
    });
    paginationContainer.appendChild(pageButton);
  }

  if (page < pageCount) {
    const nextButton = document.createElement("button");
    nextButton.textContent = "Siguiente";
    nextButton.addEventListener("click", () => {
      createTable(empleados, page + 1);
    });
    paginationContainer.appendChild(nextButton);
  }
}

function showActiveEmpleados() {
  const activos = empleadosData.filter((u) => u.estatusEmpleado === "activo");
  createTable(activos);
}

function showInactiveEmpleados() {
  const noActivos = empleadosData.filter(
    (u) => u.estatusEmpleado === "inactivo"
  );
  createTable(noActivos);
}

function showAllEmpleados() {
  createTable(empleadosData);
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
        if (usuario.role === "asesor") {
          document.getElementById("emailUserLogged").textContent =
            usuario.email;
          document.getElementById("loader").style.display = "none";
          document.getElementById("contenido").style.visibility = "visible";
          const menuLinks = document.querySelectorAll("#mySidebar a");
          if (menuLinks.length > 0) {
            menuLinks[0].remove();
          }
        } else if (usuario.role === "operacion") {
          const empleadoForm = document.getElementById("empleadoForm");
          if (empleadoForm) empleadoForm.remove();
          window.operacionMode = true;
          const tableHeader = document.getElementById("tableHeader");
          if (tableHeader && tableHeader.rows.length > 0) {
            const headerRow = tableHeader.rows[0];
            headerRow.deleteCell(-1);
            headerRow.deleteCell(-1);
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
        empleadosData = result.myArrayList.map((item) => item.map);
        createTable(empleadosData);
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

function registerEmpleado(raw) {
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
        getAllEmpleados();
        clearAll();
      }
    })
    .catch((error) => {
      let errorMsg = error;
    });
}

function deleteEmpleado(numeroTrabajador) {
  Swal.fire({
    title: "¿Quieres eliminar este empleado?",
    text: "Esta acción no podrá revertirse.",
    icon: "warning",
    showCancelButton: true,
    confirmButtonColor: "#3085d6",
    cancelButtonColor: "#d33",
    confirmButtonText: "Eliminar definitivamente",
    cancelButtonText: `Cancelar`,
  }).then((result) => {
    if (result.isConfirmed) {
      const raw = JSON.stringify({ numeroTrabajador: numeroTrabajador });

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
            getAllEmpleados();
          }
        })
        .catch((error) => {
          let errorMsg = error;
        });
    }
  });
}

function updateEmpleado(raw) {
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
        getAllEmpleados();
        clearAll();
        actualizarButtonIsActive = false;
      }
    })
    .catch((error) => {
      let errorMsg = error;
    });
}
