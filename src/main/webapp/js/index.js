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

const contenedor = document.getElementById("contenedor");

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
        return response.json(); // Si la respuesta es exitosa, manejamos los datos
      } else if (response.status === 401 || response.status === 403) {
        // Si el servidor nos dice que no estamos autorizados, redirigimos al login
        window.location.href = "/pages/login.html";
        return; // Salir del flujo para evitar otros procesamientos
      } else {
        throw new Error("Algo salió mal con la respuesta del servidor");
      }
    })
    .then((result) => {
      if (result) {
        const mainContenedor = document.getElementById("mainContenedor");

        // Eliminar contenido existente
        mainContenedor.innerHTML = "";

        // Crear el nuevo contenedor
        const gridContainer = document.createElement("div");
        gridContainer.className = "grid-container";

        // Lista de elementos
        const elementos = [
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

        // Generar las cards dinámicamente
        elementos.forEach((element) => {
          const card = document.createElement("div");
          card.className = "card";

          const title = document.createElement("h3");
          title.textContent = element.alt;

          const img = document.createElement("img");
          img.src = element.src;
          img.alt = element.alt;

          const link = document.createElement("a");
          link.href = element.url; // URL de destino

          const button = document.createElement("button");
          button.textContent = "Acceder";
          // Agregar botón dentro del enlace
          link.appendChild(button);

          card.appendChild(title);
          card.appendChild(img);
          card.appendChild(link);

          gridContainer.appendChild(card);
          document.getElementById("loader").style.display = "none"; // Oculta el loader
          document.getElementById("contenido").style.visibility = "visible";
        });

        // Añadir el nuevo contenido al contenedor principal
        mainContenedor.appendChild(gridContainer);
      }
    })
    .catch((error) => {
      console.error(error);
    });
}
