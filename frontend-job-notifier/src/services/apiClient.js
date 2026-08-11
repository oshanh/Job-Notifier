import axios from "axios";


const BASE_URL = "http://localhost:8080";


export function getUsers() {
    return axios.get(`${BASE_URL}/user/all`);
}

export function addUser(data) {
    return axios.post(`${BASE_URL}/user/add`, data);
}   

export function deleteUser(id) {
    return axios.delete(`${BASE_URL}/user/delete/${id}`);
}

export function sendTestGmail(data) {
    return axios.post(`${BASE_URL}/test/gmail`, data);
}
