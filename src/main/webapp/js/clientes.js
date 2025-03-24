const boxTable = document.getElementById("boxTable");

let url =
  window.location.hostname === "localhost"
    ? "http://localhost:8080/api/clientes"
    : "https://flotilla-mktpromomarc.onrender.com/api/clientes";

getAllClientes();

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
    .then((response) => response.json())
    .then((result) => {
      //console.log("recaptcha: "+result.recaptchaResult);
      //console.log("token: "+result.accessToken);
      result.myArrayList.forEach((item, index) => {
        // Acceder a los valores dentro de "map"
        const mapData = item.map;
        console.log(`Elemento ${index + 1}:`);
        console.log(`ID Cliente: ${mapData.idCliente}`);
        console.log(`Razón social: ${mapData.razonSocial}`);
        console.log(`RFC: ${mapData.rfc}`);
        console.log(`Estatus cliente: ${mapData.estatusCliente}`);
        console.log(`ID Usuario: ${mapData.idUsuario}`);
        console.log(`Email: ${mapData.email}`);
        console.log(`Nombre: ${mapData.nombre}`);
        console.log(`Apellido Paterno: ${mapData.apellidoPaterno}`);
        console.log(`Apellido Materno: ${mapData.apellidoMaterno}`);
        console.log(`Número trabajador: ${mapData.numeroTrabajador}`);
        console.log(`Contraseña: ${mapData.contrasena}`);
        console.log(`Estatus usuario: ${mapData.estatusUsuario}`);
        console.log(`ID tipo usuario: ${mapData.idTipoUsuario}`);
        console.log("---");
      });
    })
    .catch((error) => {
      console.error(error);
    });
}
