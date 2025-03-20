
function getData() {

const myHeaders = new Headers();
myHeaders.append("Authorization", `Bearer: ${sessionStorage.getItem("token")}`);

  const requestOptions = {
    method: "GET",
    headers: myHeaders,
    redirect: "follow",
  };

  fetch(
    "https://holaworldsagaindeployed.onrender.com/api/ServletPerson",
    requestOptions
  )
    .then((response) => response.json())
    .then((dataPerson) => {
      console.log("id Person:" + dataPerson.idPerson);
      console.log("first name:" + dataPerson.firstName);
      console.log("last name:" + dataPerson.lastName);
      console.log("direction:" + dataPerson.direction);
      console.log("role:" + dataPerson.role);
      console.log("password:" + dataPerson.password);
    })
    .catch((error) => console.error(error));
} //getDataCars()

getData();