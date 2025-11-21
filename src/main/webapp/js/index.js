let urlLogged =
  window.location.hostname === "localhost"
    ? "http://localhost:8080/api/usuarios/logged"
    : "https://flotilla-mktpromomarc.onrender.com/api/usuarios/logged";

// Urls para modulos
let urlClientes =
  window.location.hostname === "localhost"
    ? "http://localhost:8080/pages/clientes.html"
    : "https://flotilla-mktpromomarc.onrender.com/pages/clientes.html";

let urlUsuarios =
  window.location.hostname === "localhost"
    ? "http://localhost:8080/pages/usuarios.html"
    : "https://flotilla-mktpromomarc.onrender.com/pages/usuarios.html";

let urlEmpleados =
  window.location.hostname === "localhost"
    ? "http://localhost:8080/pages/empleados.html"
    : "https://flotilla-mktpromomarc.onrender.com/pages/empleados.html";

let urlVehiculos =
  window.location.hostname === "localhost"
    ? "http://localhost:8080/pages/vehiculos.html"
    : "https://flotilla-mktpromomarc.onrender.com/pages/vehiculos.html";

let urlArrendamientos =
  window.location.hostname === "localhost"
    ? "http://localhost:8080/pages/arrendamientos.html"
    : "https://flotilla-mktpromomarc.onrender.com/pages/arrendamientos.html";

let urlPlacas =
  window.location.hostname === "localhost"
    ? "http://localhost:8080/pages/placas.html"
    : "https://flotilla-mktpromomarc.onrender.com/pages/placas.html";

let urlSeguros =
  window.location.hostname === "localhost"
    ? "http://localhost:8080/pages/seguros.html"
    : "https://flotilla-mktpromomarc.onrender.com/pages/seguros.html";

let urlAsignaciones =
  window.location.hostname === "localhost"
    ? "http://localhost:8080/pages/asignaciones.html"
    : "https://flotilla-mktpromomarc.onrender.com/pages/asignaciones.html";

let urlServicios =
  window.location.hostname === "localhost"
    ? "http://localhost:8080/pages/servicios.html"
    : "https://flotilla-mktpromomarc.onrender.com/pages/servicios.html";

let urlIncidentes =
  window.location.hostname === "localhost"
    ? "http://localhost:8080/pages/incidentes.html"
    : "https://flotilla-mktpromomarc.onrender.com/pages/incidentes.html";

if (window.location.hostname === "3.149.10.58") {
  urlIncidentes = "http://3.149.10.58:8080/pages/incidentes.html";
  urlServicios = "http://3.149.10.58:8080/pages/servicios.html";
  urlAsignaciones = "http://3.149.10.58:8080/pages/asignaciones.html";
  urlLogged = "http://3.149.10.58:8080/api/usuarios/logged";
  urlClientes = "http://3.149.10.58:8080/pages/clientes.html";
  urlUsuarios = "http://3.149.10.58:8080/pages/usuarios.html";
  urlEmpleados = "http://3.149.10.58:8080/pages/empleados.html";
  urlVehiculos = "http://3.149.10.58:8080/pages/vehiculos.html";
  urlArrendamientos = "http://3.149.10.58:8080/pages/arrendamientos.html";
  urlPlacas = "http://3.149.10.58:8080/pages/placas.html";
  urlSeguros = "http://3.149.10.58:8080/pages/seguros.html";
}

const contenedor = document.getElementById("contenedor");
const sidePanel = document.getElementById("sidePanel");
const vehiculosCount = document.getElementById("vehiculosCount");
const arrendamientosCount = document.getElementById("arrendamientosCount");
const placasCount = document.getElementById("placasCount");
const segurosCount = document.getElementById("segurosCount");
const asignacionesCount = document.getElementById("asignacionesCount");

window.addEventListener("pageshow", function (event) {
  if (event.persisted) {
    window.location.reload();
  }
});

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
      } else if (response.status === 401 || response.status === 403) {
        window.location.href = "/pages/login.html";
        return;
      } else {
        throw new Error("Algo salió mal con la respuesta del servidor");
      }
    })
    .then((usuario) => {
      if (usuario) {
        const mainContenedor = document.getElementById("mainContenedor");

        mainContenedor.innerHTML = "";

        //const gridContainer = document.createElement("div");
        //gridContainer.className = "grid-container";

        let elementos = [
          {
            src: "https://res.cloudinary.com/dseuvfwyj/image/upload/v1743360169/index/jya9xdjmykyrs1hlf7g5.svg",
            alt: "Usuarios",
            url: urlUsuarios,
          },
          {
            src: "https://res.cloudinary.com/dseuvfwyj/image/upload/v1743360167/index/rkfoyclcomgcp5hqkpor.svg",
            alt: "Clientes",
            url: urlClientes,
          },
          {
            src: "https://res.cloudinary.com/dseuvfwyj/image/upload/v1743360167/index/ztyff48wgvofmymyzzg8.svg",
            alt: "Empleados",
            url: urlEmpleados,
          },
          {
            src: "https://res.cloudinary.com/dseuvfwyj/image/upload/v1743360169/index/aqw3g4xstvhcqkl9nyst.svg",
            alt: "Vehículos",
            url: urlVehiculos,
          },
          {
            src: "https://res.cloudinary.com/dseuvfwyj/image/upload/v1743360168/index/nwpmiudui5iiohpqqwnw.svg",
            alt: "Arrendamientos",
            url: urlArrendamientos,
          },
          {
            src: "https://res.cloudinary.com/dseuvfwyj/image/upload/v1743360169/index/j4dstfoegtyr3jks49dl.svg",
            alt: "Placas",
            url: urlPlacas,
          },
          {
            src: "https://res.cloudinary.com/dseuvfwyj/image/upload/v1743360167/index/b4ypmxkcvmdzucgkqvwn.svg",
            alt: "Seguros",
            url: urlSeguros,
          },
          {
            src: "https://res.cloudinary.com/dseuvfwyj/image/upload/v1743360167/index/ohzobq6vfto0lvdu4ui0.svg",
            alt: "Asignaciones",
            url: urlAsignaciones,
          },
          {
            src: "https://res.cloudinary.com/dseuvfwyj/image/upload/v1743360167/index/e20dkkj9gbgkifklphs6.svg",
            alt: "Servicios",
            url: urlServicios,
          },
          {
            src: "https://res.cloudinary.com/dseuvfwyj/image/upload/v1743360167/index/csep0acxeu8ddy9enatu.svg",
            alt: "Incidentes",
            url: urlIncidentes,
          },
        ];

        if (usuario.role === "Operación") {
          elementos.shift();
        }
        if (usuario.role === "Asesor") {
          elementos.shift();
          elementos.shift();
        }
        //      if (usuario.role === "Administrador") {
        //          elementos = [elementos[0]];
        //        }

        elementos.forEach((element) => {
          const anchor = document.createElement("a");
          anchor.href = element.url;
          const divFlex = document.createElement("div");
          divFlex.style.display = "flex";
          divFlex.style.alignItems = "center";

          const img = document.createElement("img");
          img.src = element.src;
          img.alt = element.alt;
          divFlex.appendChild(img);

          const title = document.createElement("span");
          title.textContent = element.alt;
          title.style.marginLeft = "15px";
          divFlex.appendChild(title);

          anchor.appendChild(divFlex);

          sidePanel.appendChild(anchor);
          //gridContainer.appendChild(card);
          document.getElementById("emailUserLogged").textContent =
            usuario.email;
          document.getElementById("loader").style.display = "none";
          document.getElementById("contenidoIndex").style.visibility =
            "visible";
          sidePanel.style.display = "block";
        });

        //sidePanel.appendChild(gridContainer);

        getVehiculosCount();
        getServiciosCount();
        getIncidentesCount();
        setTimeout(() => {
          const months = {
            jan: 0,
            feb: 1,
            mar: 2,
            apr: 3,
            may: 4,
            jun: 5,
            jul: 6,
            aug: 7,
            sep: 8,
            oct: 9,
            nov: 10,
            dec: 11,
          };

          const serviciosDataFiltered = serviciosCountData.map((item) => {
            const clean = item.fecha.replace(",", "").split(" ");

            const month = months[clean[0].toLowerCase()];
            const day = parseInt(clean[1]);
            const year = parseInt(clean[2]);

            return [new Date(year, month, day), item.totalservicios];
          });

          new Dygraph(
            document.getElementById("graficaServicios"),
            serviciosDataFiltered,
            {
              labels: ["Fecha", "Servicios"],
              title: "Servicios última semana",
              ylabel: "Total",
              xlabel: "Fecha",
              fillGraph: true,
              strokeWidth: 2,
              drawPoints: true,
              pointSize: 3,
              axisLabelFontFamily: "Arial",
            }
          );

          const incidentesDataFiltered = incidentesCountData.map((item) => {
            const clean = item.fecha.replace(",", "").split(" ");

            const month = months[clean[0].toLowerCase()];
            const day = parseInt(clean[1]);
            const year = parseInt(clean[2]);

            return [new Date(year, month, day), item.totalincidentes];
          });

          new Dygraph(
            document.getElementById("graficaIncidentes"),
            incidentesDataFiltered,
            {
              labels: ["Fecha", "Incidentes"],
              title: "Incidentes última semana",
              ylabel: "Total",
              fillGraph: true,
              xlabel: "Fecha",
              strokeWidth: 2,
              drawPoints: true,
              pointSize: 3,
              axisLabelFontFamily: "Arial",
            }
          );
        }, 2000);
      }
    })
    .catch((error) => {
      console.error(error);
    });
}

document
  .getElementById("clickToLogOut")
  .addEventListener("click", function (event) {
    event.preventDefault();
    sessionStorage.removeItem("token");
    window.location.href = "/pages/login.html";
  });

function getVehiculosCount() {
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

  fetch(urlVehiculosCount, requestOptions)
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
        vehiculosCountData = result.myArrayList.map((item) => item.map);
        // Vehículos
        vehiculosCount.appendChild(
          createSpan(vehiculosCountData[0].totalvehiculos)
        );

        // Asignaciones
        asignacionesCount.appendChild(
          createSpan(vehiculosCountData[0].totalasignaciones)
        );

        // Arrendamientos
        const arrImg =
          vehiculosCountData[0].totalarrendamientos !=
          vehiculosCountData[0].totalvehiculos
            ? createIcon("/images/uncheck.png")
            : createIcon("/images/check.png");

        arrendamientosCount.appendChild(arrImg);
        arrendamientosCount.appendChild(
          createSpan(vehiculosCountData[0].totalarrendamientos)
        );

        // Placas
        const plaImg =
          vehiculosCountData[0].totalplacas !=
          vehiculosCountData[0].totalvehiculos
            ? createIcon("/images/uncheck.png")
            : createIcon("/images/check.png");

        placasCount.appendChild(plaImg);
        placasCount.appendChild(createSpan(vehiculosCountData[0].totalplacas));

        // Seguros
        const segImg =
          vehiculosCountData[0].totalseguros !=
          vehiculosCountData[0].totalvehiculos
            ? createIcon("/images/uncheck.png")
            : createIcon("/images/check.png");

        segurosCount.appendChild(segImg);
        segurosCount.appendChild(
          createSpan(vehiculosCountData[0].totalseguros)
        );
      }
    })
    .catch((error) => {
      let errorMsg = error;
    });
}

function getServiciosCount() {
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

  fetch(urlServiciosCount, requestOptions)
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
        serviciosCountData = result.myArrayList.map((item) => item.map);
        console.log(serviciosCountData);
      }
    })
    .catch((error) => {
      let errorMsg = error;
    });
}

function getIncidentesCount() {
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

  fetch(urlIncidentesCount, requestOptions)
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
        incidentesCountData = result.myArrayList.map((item) => item.map);
        console.log(incidentesCountData);
      }
    })
    .catch((error) => {
      let errorMsg = error;
    });
}
let urlVehiculosCount = "/api/login/vehiculosCount";
let vehiculosCountData = [];
let urlIncidentesCount = "/api/login/incidentesCount";
let incidentesCountData = [];
let urlServiciosCount = "/api/login/serviciosCount";
let serviciosCountData = [];

function createIcon(src) {
  const img = document.createElement("img");
  img.src = src;
  img.style.width = "30px";
  img.style.height = "30px";
  return img;
}

function createSpan(text) {
  const span = document.createElement("span");
  span.textContent = text;
  span.style.fontSize = "40px";
  span.style.fontWeight = "bold";
  return span;
}
