import axios from "axios";

const API_URL = "http://localhost:8080/api/auth";
export default {
  login(email, password) {
    return axios.post(`${API_URL}/login`, { email, password });
  },
  register(user) {
    return axios.post(`${API_URL}/register`, user);
  },
};
