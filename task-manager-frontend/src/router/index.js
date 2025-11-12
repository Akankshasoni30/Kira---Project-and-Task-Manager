import Vue from "vue";
import Router from "vue-router";
import LoginRegisterPage from "@/views/LoginRegister.vue";
import MainLayout from "@/layout/MainLayout.vue";
import DashboardPage from "@/views/DashboardPage.vue";
import ManageProjectsPage from "@/views/ManageProjects.vue";
import CreateTaskPage from "@/views/CreateTaskPage.vue";
Vue.use(Router);
export default new Router({
  mode: "history",
  routes: [
    { path: "/", name: "LoginRegister", component: LoginRegisterPage },
    {
      path: "/layout",
      component: MainLayout,
      children: [
        { path: "", name: "Dashboard", component: DashboardPage },
        {
          path: "projects",
          name: "ManageProjects",
          component: ManageProjectsPage,
        },
        { path: "tasks/create", name: "CreateTask", component: CreateTaskPage },
      ],
    },
  ],
});
