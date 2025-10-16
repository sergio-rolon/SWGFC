let clientesData = [];
let rowsFiltered = [];
let urlLogged = "/api/usuarios/logged";
let url = "/api/clientes";
let urlAsesores = "/api/usuarios/getAllAsesores";
let actualizarButtonIsActive = false;
let currentPage = 1;
const rowsPerPage = 5;

const contenedor = document.getElementById("contenedor");
const tbody = document.getElementById("tableBody");

const razonSocialError = document.getElementById("razonSocialError");
const rfcError = document.getElementById("rfcError");
const idUsuarioError = document.getElementById("idUsuarioError");
const idTipoEstatusError = document.getElementById("idTipoEstatusError");

const idCliente = document.getElementById("idCliente");
const razonSocial = document.getElementById("razonSocial");
const rfc = document.getElementById("rfc");
const idUsuario = document.getElementById("idUsuario");
const idTipoEstatus = document.getElementById("idTipoEstatus");
const idUsuarioSelect = document.getElementById("idUsuarioSelect");
// *********************Execution at start
window.addEventListener("pageshow", function (event) {
  if (event.persisted) {
    window.location.reload();
  }
});
validateLogin();
getAllClientes();
getAllAsesores();

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
        razonSocial: razonSocial.value,
        rfc: rfc.value,
        idTipoEstatus: idTipoEstatus.value,
        idUsuario: idUsuarioSelect.value,
      });

      registerCliente(raw);
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
          idCliente: idCliente.value,
          razonSocial: razonSocial.value,
          rfc: rfc.value,
          idTipoEstatus: idTipoEstatus.value,
          idUsuario: idUsuarioSelect.value,
        });
        updateCliente(raw);
      }
    } else {
      Swal.fire({
        title: "Operación inválida",
        text: "Elige primero un cliente para editar",
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
    "Id Cliente",
    "Razón Social",
    "RFC",
    "Estatus Cliente",
    "Número Trabajador",
    "Nombre",
    "Apellido Paterno",
    "Apellido Materno",
    "Estatus Usuario",
  ];

  const rows = clientesData.map((cliente) => [
    cliente.idCliente,
    cliente.razonSocial,
    cliente.rfc,
    cliente.estatusCliente,
    cliente.numeroTrabajador,
    cliente.nombre,
    cliente.apellidoPaterno,
    cliente.apellidoMaterno,
    cliente.estatusUsuario,
  ]);

  const workSheet = XLSX.utils.aoa_to_sheet([headers, ...rows]);

  const workBook = XLSX.utils.book_new();
  XLSX.utils.book_append_sheet(workBook, workSheet, "Clientes");

  XLSX.writeFile(workBook, "clientes_reporte.xlsx");
}

function populateSecondDropdown() {
  const secondDropdown = document.getElementById("secondDropdown");
  secondDropdown.innerHTML = "";

  const uniqueAsesores = [
    ...new Set(
      clientesData.map(
        (c) => `${c.numeroTrabajador} - ${c.nombre} ${c.apellidoPaterno}`
      )
    ),
  ];

  uniqueAsesores.forEach((asesor) => {
    const label = document.createElement("label");
    label.innerHTML = `
      <input type="checkbox" class="second-filter" value="${asesor}" checked /> ${asesor}
    `;
    secondDropdown.appendChild(label);
    secondDropdown.appendChild(document.createElement("br"));
  });
}

function filterSelection() {
  const selectedEstatus = Array.from(
    document.querySelectorAll(".estatus-filter:checked")
  ).map((cb) => cb.value);
  const selectedAsesores = Array.from(
    document.querySelectorAll(".second-filter:checked")
  ).map((cb) => cb.value);

  const filteredSelection = clientesData.filter(
    (cliente) =>
      selectedEstatus.includes(cliente.estatusCliente) &&
      selectedAsesores.includes(
        `${cliente.numeroTrabajador} - ${cliente.nombre} ${cliente.apellidoPaterno}`
      )
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
  razonSocialError.textContent = "";
  razonSocial.classList.remove("borde-rojo");

  rfcError.textContent = "";
  rfc.classList.remove("borde-rojo");
}

function validateNull() {
  let flag = false;
  if (!razonSocial.value || razonSocial.value.trim() === "") {
    razonSocialError.textContent = "Razón social no puede ser nulo";
    razonSocial.classList.add("borde-rojo");
    flag = true;
  }
  if (!rfc.value || rfc.value.trim() === "") {
    rfcError.textContent = "RFC no puede ser nulo";
    rfc.classList.add("borde-rojo");
    flag = true;
  }

  return flag;
}

function clearForm() {
  razonSocial.value = "";
  rfc.value = "";
  idTipoEstatus.value = "1";
  idUsuarioSelect.value = "7";
  idCliente.value = "";
  actualizarButtonIsActive = false;
}

function setErrorMsgs(result) {
  if (result["Razonsocial"] && result.Razonsocial != "success") {
    razonSocialError.textContent = result.Razonsocial;
  }
  if (result["rfc"] && result.rfc != "success") {
    rfcError.textContent = result.rfc;
  }
  if (result["idTipoEstatus"] && result.idTipoEstatus != "success") {
    idTipoEstatusError.textContent = result.idTipoEstatus;
  }
  if (result["idUsuario"] && result.idUsuario != "success") {
    idUsuarioError.textContent = result.idUsuario;
  }
}

function editeCliente(clienteString) {
  const elementTop =
    document.getElementById("main").getBoundingClientRect().top +
    window.scrollY;
  window.scrollTo({
    top: elementTop - 46,
    behavior: "smooth",
  });
  clearAll();
  const cliente = JSON.parse(clienteString);
  idCliente.value = cliente.idCliente;
  razonSocial.value = cliente.razonSocial;
  rfc.value = cliente.rfc;
  if (cliente.estatusCliente == "Activo") {
    idTipoEstatus.value = 1;
  } else {
    idTipoEstatus.value = 2;
  }
  idUsuarioSelect.value = cliente.idUsuario;

  actualizarButtonIsActive = true;
}

function createTable(clientes, page = 1) {
  tbody.innerHTML = "";

  const startIndex = (page - 1) * rowsPerPage;
  const endIndex = startIndex + rowsPerPage;
  const paginatedClientes = clientes.slice(startIndex, endIndex);

  paginatedClientes.forEach((cliente) => {
    const row = document.createElement("tr");
    const clienteString = JSON.stringify(cliente).replace(/"/g, "&quot;");
    row.innerHTML = `
          <td>${cliente.idCliente}</td>
          <td>${cliente.razonSocial}</td>
          <td>${cliente.rfc}</td>
          <td>${cliente.estatusCliente}</td>
          <td>${cliente.numeroTrabajador}</td>
          <td>${cliente.nombre}</td>
          <td>${cliente.apellidoPaterno}</td>
          <td>${cliente.apellidoMaterno}</td>
          <td>${cliente.estatusUsuario}</td>
          <td><button class="edit-btn" onclick="editeCliente('${clienteString}')">Editar</button></td>
          <td><button class="delete-btn" onclick="deleteCliente('${cliente.rfc}')">Eliminar</button></td>
          `;

    tbody.appendChild(row);
  });

  renderPagination(clientes, page);
}

function renderPagination(clientes, page) {
  const paginationContainer = document.getElementById("paginationDiv");
  paginationContainer.innerHTML = "";

  const pageCount = Math.ceil(clientes.length / rowsPerPage);

  if (page > 1) {
    const prevButton = document.createElement("button");
    prevButton.textContent = "Anterior";
    prevButton.addEventListener("click", () => {
      createTable(clientes, page - 1);
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
      createTable(clientes, i);
    });
    paginationContainer.appendChild(pageButton);
  }

  if (page < pageCount) {
    const nextButton = document.createElement("button");
    nextButton.textContent = "Siguiente";
    nextButton.addEventListener("click", () => {
      createTable(clientes, page + 1);
    });
    paginationContainer.appendChild(nextButton);
  }
}

function showActiveClientes() {
  const activos = clientesData.filter((u) => u.estatusCliente === "Activo");
  createTable(activos);
}

function showInactiveClientes() {
  const noActivos = clientesData.filter((u) => u.estatusCliente === "Inactivo");
  createTable(noActivos);
}

function showAllClientes() {
  const checkboxes1 = document.querySelectorAll(".estatus-filter");
  checkboxes1.forEach((chk) => (chk.checked = true));

  const checkboxes2 = document.querySelectorAll(".second-filter");
  checkboxes2.forEach((chk) => (chk.checked = true));

  createTable(clientesData);
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
        } else {
          window.location.href = "/index.html";
        }
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
        clientesData = result.myArrayList.map((item) => item.map);
        rowsFiltered = [...clientesData];
        createTable(clientesData);
        populateSecondDropdown();
      }
    })
    .catch((error) => {
      let errorMsg = error;
    });
}

function getAllAsesores() {
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

  fetch(urlAsesores, requestOptions)
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
        idUsuarioSelect.innerHTML = "";
        let asesores = result.myArrayList.map((item) => item.map);
        asesores.forEach((asesor) => {
          const option = document.createElement("option");
          option.value = asesor.idUsuario;
          option.textContent = `${asesor.numeroTrabajador} - ${asesor.nombre} ${asesor.apellidoPaterno} ${asesor.apellidoMaterno}`;
          idUsuarioSelect.appendChild(option);
        });
      }
    })
    .catch((error) => {
      let errorMsg = error;
    });
}

function registerCliente(raw) {
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
        getAllClientes();
        //getAllAsesores();
        clearAll();
      }
    })
    .catch((error) => {
      let errorMsg = error;
    });
}

function deleteCliente(rfc) {
  Swal.fire({
    title: "¿Quieres eliminar este cliente?",
    text: "Esta acción no podrá revertirse.",
    icon: "warning",
    showCancelButton: true,
    confirmButtonColor: "#3085d6",
    cancelButtonColor: "#d33",
    confirmButtonText: "Eliminar definitivamente",
    cancelButtonText: `Cancelar`,
  }).then((result) => {
    if (result.isConfirmed) {
      const raw = JSON.stringify({ rfc: rfc });

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
            getAllClientes();
            //getAllAsesores();
          }
        })
        .catch((error) => {
          let errorMsg = error;
        });
    }
  });
}

function updateCliente(raw) {
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
        getAllClientes();
        //getAllAsesores();
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

  rowsFiltered = clientesData.filter((cliente) =>
    cliente.rfc.toLowerCase().includes(searchedValue)
  );

  createTable(rowsFiltered, 1);
});
