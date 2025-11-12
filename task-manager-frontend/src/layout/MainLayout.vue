<template>
  <v-app>
    <v-navigation-drawer
      app
      v-model="drawer"
      :width="drawerWidth"
      clipped
      fixed
      class="modern-drawer"
    >
      <div class="drawer-header text-center">
        <v-img
          src="@/assets/kirasmall.png"
          max-width="70"
          contain
          class="mx-auto mb-2"
        ></v-img>
      </div>
      <v-sheet elevation="0" class="profile-card mx-2 mb-4 pa-3">
        <v-row no-gutters align="center">
          <v-col cols="4" class="d-flex justify-center">
            <div class="avatar-container">
              <v-avatar size="56">
                <v-img :src="getAvatar(profile.role, profile.avatar)"></v-img>
              </v-avatar>
              <span
                class="status-dot"
                :class="
                  profile.status?.toLowerCase() === 'online'
                    ? 'online'
                    : 'offline'
                "
              ></span>
            </div>
          </v-col>
          <v-col
            cols="8"
            class="d-flex flex-column justify-center align-start pl-2"
          >
            <div class="profile-name">{{ profile.name }}</div>
            <div class="profile-email">{{ profile.email }}</div>
            <v-chip
              :color="roleColor(profile.role)"
              small
              outlined
              class="role-chip mt-1"
            >
              {{ profile.role }}
            </v-chip>
          </v-col>
        </v-row>
      </v-sheet>
      <v-list dense nav class="menu-list">
        <v-list-item
          v-for="item in menuItems"
          :key="item.title"
          @click="selectMenu(item)"
          :active="selectedMenu.title === item.title"
          class="menu-item"
        >
          <v-list-item-icon>
            <v-icon>{{ item.icon }}</v-icon>
          </v-list-item-icon>
          <v-list-item-content>
            <v-list-item-title>{{ item.title }}</v-list-item-title>
          </v-list-item-content>
        </v-list-item>
        <v-divider class="my-2"></v-divider>
        <v-list-item @click="logout" class="logout-item">
          <v-list-item-icon>
            <v-icon>mdi-logout</v-icon>
          </v-list-item-icon>
          <v-list-item-content>
            <v-list-item-title>Logout</v-list-item-title>
          </v-list-item-content>
        </v-list-item>
      </v-list>
      <div class="resize-handle" @mousedown="startResize"></div>
    </v-navigation-drawer>
    <v-app-bar app color="#0052CC" dark>
      <v-app-bar-nav-icon @click="drawer = !drawer"></v-app-bar-nav-icon>
      <v-toolbar-title class="cursive-text">{{
        selectedMenu.title
      }}</v-toolbar-title>
      <v-spacer></v-spacer>
      <v-btn icon @click="toggleTheme">
        <v-icon>
          {{
            $vuetify.theme.dark
              ? "mdi-white-balance-sunny"
              : "mdi-moon-waxing-crescent"
          }}
        </v-icon>
      </v-btn>
    </v-app-bar>
    <v-main>
      <v-container fluid class="main-content">
        <component :is="selectedMenu.component"></component>
      </v-container>
    </v-main>
  </v-app>
</template>
<script>
import DashboardPage from "@/views/DashboardPage.vue";
import ManageProjectsPage from "@/views/ManageProjects.vue";
import CreateTaskPage from "@/views/CreateTaskPage.vue";
export default {
  name: "MainLayout",
  data() {
    return {
      drawer: true,
      drawerWidth: 260,
      profile: {
        name: "John Doe",
        email: "john@example.com",
        role: "Admin",
        avatar: "",
        status: "online",
      },
      menuItems: [
        {
          title: "Dashboard",
          icon: "mdi-view-dashboard",
          component: DashboardPage,
        },
        {
          title: "Manage Projects",
          icon: "mdi-folder",
          component: ManageProjectsPage,
        },
        {
          title: "Manage Tasks",
          icon: "mdi-plus-box",
          component: CreateTaskPage,
        },
      ],
      selectedMenu: { title: "Dashboard", component: DashboardPage },
      resizing: false,
      startX: 0,
      startWidth: 260,
    };
  },
  created() {
    this.loadProfile();
    const dark = localStorage.getItem("darkTheme");
    if (dark !== null) {
      this.$vuetify.theme.dark = dark === "true";
    }
  },
  methods: {
    getAvatar(role, currentAvatar) {
      if (currentAvatar) return currentAvatar;
      const adminAvatar = "https://cdn.vuetifyjs.com/images/john.jpg";
      const userAvatars = ["https://randomuser.me/api/portraits/men/32.jpg"];
      if (role.toLowerCase() === "admin") return adminAvatar;
      if (role.toLowerCase() === "user") {
        return userAvatars[Math.floor(Math.random() * userAvatars.length)];
      }
      return "https://cdn.vuetifyjs.com/images/john.jpg";
    },
    loadProfile() {
      const storedProfile = localStorage.getItem("profile");
      if (storedProfile) {
        try {
          this.profile = JSON.parse(storedProfile);
        } catch (err) {
          console.error("Failed to load profile:", err);
        }
      }
    },
    selectMenu(item) {
      this.selectedMenu = item;
    },
    logout() {
      localStorage.removeItem("token");
      localStorage.removeItem("profile");
      this.$router.push("/");
    },
    roleColor(role) {
      switch (role.toLowerCase()) {
        case "admin":
          return "red lighten-1";
        case "user":
          return "blue lighten-1";
        case "manager":
          return "purple lighten-1";
        default:
          return "grey lighten-1";
      }
    },
    goToProfile() {
      this.$router.push("/profile");
    },
    goToSettings() {
      this.$router.push("/settings");
    },
    startResize(e) {
      this.resizing = true;
      this.startX = e.clientX;
      this.startWidth = this.drawerWidth;
      document.addEventListener("mousemove", this.doResize);
      document.addEventListener("mouseup", this.stopResize);
    },
    doResize(e) {
      if (this.resizing) {
        let newWidth = this.startWidth + (e.clientX - this.startX);
        this.drawerWidth = Math.min(Math.max(newWidth, 220), 400);
      }
    },
    stopResize() {
      this.resizing = false;
      document.removeEventListener("mousemove", this.doResize);
      document.removeEventListener("mouseup", this.stopResize);
    },
    toggleTheme() {
      this.$vuetify.theme.dark = !this.$vuetify.theme.dark;
      localStorage.setItem("darkTheme", this.$vuetify.theme.dark);
    },
  },
};
</script>
<style scoped>
.modern-drawer {
  transition: width 0.2s ease;
}
.profile-card {
  background: var(--v-theme-surface);
  color: var(--v-theme-on-surface);
  padding: 12px;
  margin: 12px;
  border-radius: 10px;
}
.v-avatar {
  position: relative;
}
.menu-item.v-list-item--active {
  background-color: #0052cc !important;
  border-radius: 6px;
}
.menu-item:hover {
  background-color: rgba(0, 82, 204, 0.2);
  border-radius: 6px;
}
.logout-item {
  margin-top: auto;
}
.resize-handle {
  width: 5px;
  cursor: ew-resize;
  position: absolute;
  top: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.1);
}
.cursive-text {
  font-family: "Dancing Script", cursive;
}
.main-content {
  background-color: var(--v-theme-background);
  min-height: 100vh;
  padding: 16px;
}
.theme--light .modern-drawer {
  background-color: #ffffff;
  color: #333;
}
.theme--dark .modern-drawer {
  background-color: #1e2a38;
  color: #fff;
}
.profile-card {
  background: #2c3e50;
  color: #fff;
  padding: 12px;
  margin: 12px;
  border-radius: 10px;
}
.profile-info {
  display: flex;
  flex-direction: column;
  justify-content: center;
  gap: 4px;
}
.profile-name {
  font-size: 16px;
  font-weight: bold;
}
.profile-email {
  font-size: 14px;
  opacity: 0.8;
}
.avatar-container {
  position: relative;
  display: inline-block;
}
.status-dot {
  width: 12px;
  height: 12px;
  border-radius: 50%;
  border: 2px solid #2c3e50;
  position: absolute;
  bottom: 4px;
  right: 4px;
  box-shadow: 0 0 4px rgba(0, 0, 0, 0.4);
}
.status-dot.online {
  background-color: #00e676;
}
.status-dot.offline {
  background-color: #9e9e9e;
}
</style>
