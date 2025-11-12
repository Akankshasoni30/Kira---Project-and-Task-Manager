import Vue from "vue";
import App from "./App.vue";
import router from "./router";
import Vuetify from "vuetify";
import "vuetify/dist/vuetify.min.css";


import VueApexCharts from "vue-apexcharts";
Vue.use(VueApexCharts);
Vue.component("apex-chart", VueApexCharts);

Vue.use(Vuetify);
Vue.config.productionTip = false;


new Vue({
  router,
  vuetify: new Vuetify(),
  render: (h) => h(App),
}).$mount("#app");
