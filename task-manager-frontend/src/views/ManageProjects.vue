<template>
  <v-container fluid class="pa-6 modern-container">
    <div class="d-flex justify-space-between align-center mb-6 flex-wrap">
      <div>
        <p class="text-subtitle-2 grey--text text--darken-1 mt-1">
          💡 Manage, track and monitor all your ongoing projects at a glance.
        </p>
      </div>
      <div class="d-flex align-center flex-wrap">
        <v-text-field
          v-model="searchQuery"
          placeholder="Search by project name..."
          outlined
          dense
          rounded
          clearable
          prepend-inner-icon="mdi-magnify"
          hide-details
          color="primary"
          class="mr-3 search-field"
        ></v-text-field>
        <v-btn
          v-if="isAdmin"
          color="primary"
          class="elevation-3 rounded-lg"
          @click="openCreateDialog"
        >
          <v-icon left>mdi-plus</v-icon>
          Add Project
        </v-btn>
      </div>
    </div>
    <v-row dense class="project-grid">
      <transition-group name="fade" tag="div" class="project-grid-inner">
        <v-col
          v-for="project in filteredProjects"
          :key="project.id"
          cols="12"
          sm="6"
          md="4"
          class="mb-4"
        >
          <v-card
            class="project-card hoverable elevation-5 rounded-xl transition-ease"
            @click="openProject(project)"
          >
            <div
              class="card-header pa-4 d-flex justify-space-between align-center"
            >
              <div>
                <h3 class="text-h6 font-weight-bold white--text">
                  {{ project.name }}
                </h3>
                <p class="text-caption white--text description-wrap">
                  {{ project.description || "No description" }}
                </p>
              </div>

              <v-avatar size="36" color="white" class="elevation-2">
                <v-icon color="primary">mdi-folder</v-icon>
              </v-avatar>
            </div>
            <v-card-text class="pa-4">
              <div class="d-flex justify-space-between align-center mb-3">
                <v-chip small color="primary" dark>
                  {{ taskCounts[project.id] || 0 }} Tasks
                </v-chip>
                <v-chip
                  v-if="taskCounts[project.id] > 5"
                  small
                  color="green"
                  dark
                  class="font-weight-medium"
                >
                  Active
                </v-chip>
              </div>
              <div class="mb-3">
                <div class="d-flex justify-space-between mb-1">
                  <span class="text-caption grey--text text--darken-1"
                    >Progress</span
                  >
                  <span class="text-caption font-weight-medium">
                    {{ getProjectProgress(project) }}%
                  </span>
                </div>
                <v-progress-linear
                  :value="getProjectProgress(project)"
                  height="7"
                  rounded
                  color="primary"
                  striped
                  stream
                ></v-progress-linear>
              </div>
              <div class="d-flex align-center mt-3">
                <v-avatar
                  v-for="(member, i) in getProjectMembers(project)"
                  :key="i"
                  size="32"
                  class="mr-1"
                >
                  <img :src="member.avatar" :alt="member.name" />
                </v-avatar>
                <span
                  v-if="getProjectMembers(project).length === 0"
                  class="text-caption grey--text text--darken-1"
                >
                  No members
                </span>
              </div>
            </v-card-text>
            <v-divider></v-divider>
            <v-card-actions v-if="isAdmin" class="justify-end pr-2">
              <v-btn
                icon
                small
                color="blue"
                @click.stop="openEditDialog(project)"
              >
                <v-icon small>mdi-pencil</v-icon>
              </v-btn>
              <v-btn
                icon
                small
                color="primary"
                @click.stop="confirmDelete(project)"
              >
                <v-icon small>mdi-delete</v-icon>
              </v-btn>
            </v-card-actions>
          </v-card>
        </v-col>
      </transition-group>
    </v-row>
    <v-dialog v-model="dialog" max-width="900px" persistent>
      <v-card class="pa-4 rounded-lg">
        <v-card-title class="d-flex justify-space-between align-center">
          <div>
            <h3 class="text-h6 font-weight-bold">
              {{ selectedProject?.name }}
            </h3>
            <p class="text-caption text--secondary">
              {{ selectedProject?.description }}
            </p>
          </div>
          <v-btn icon @click="dialog = false"><v-icon>mdi-close</v-icon></v-btn>
        </v-card-title>
        <v-divider class="my-2"></v-divider>
        <v-card-text>
          <v-data-table
            dense
            :headers="taskHeaders"
            :items="projectTasks"
            class="elevation-1"
            no-data-text="No tasks available for this project."
          >
            <template slot="item.status" slot-scope="{ item }">
              <v-chip :color="getStatusColor(item.status)" small dark>
                {{ item.status === "PENDING" ? "New" : item.status }}
              </v-chip>
            </template>

            <template slot="item.priority" slot-scope="{ item }">
              <v-chip :color="getPriorityColor(item.priority)" small dark>
                {{ item.priority }}
              </v-chip>
            </template>
          </v-data-table>
        </v-card-text>
      </v-card>
    </v-dialog>
    <v-dialog v-if="isAdmin" v-model="editDialog" max-width="600px">
      <v-card class="pa-4 rounded-lg">
        <v-card-title class="font-weight-bold">
          {{ editMode ? "Edit Project" : "Create Project" }}
        </v-card-title>
        <v-card-text>
          <v-text-field
            v-model="projectForm.name"
            label="Project Name"
            outlined
            dense
          />
          <v-textarea
            v-model="projectForm.description"
            label="Description"
            outlined
            rows="3"
            dense
          />
        </v-card-text>
        <v-card-actions class="justify-end">
          <v-btn text @click="editDialog = false">Cancel</v-btn>
          <v-btn color="primary" @click="saveProject">
            {{ editMode ? "Update" : "Create" }}
          </v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
    <v-dialog v-model="deleteDialog" max-width="400px">
      <v-card class="pa-4 rounded-lg">
        <v-card-title class="headline font-weight-bold">
          Confirm Delete
        </v-card-title>
        <v-card-text>
          Are you sure you want to delete
          <strong>{{ projectToDelete?.name }}</strong
          >?
        </v-card-text>
        <v-card-actions class="justify-end">
          <v-btn text @click="deleteDialog = false">Cancel</v-btn>
          <v-btn
            color="primary"
            dark
            @click="deleteProject(projectToDelete.id)"
          >
            Delete
          </v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
    <v-snackbar
      v-model="snackbar.show"
      :color="snackbar.color"
      top
      right
      elevation="5"
      timeout="3000"
    >
      {{ snackbar.text }}
      <template v-slot:action="{ attrs }">
        <v-btn text v-bind="attrs" @click="snackbar.show = false">
          <v-icon>mdi-close</v-icon>
        </v-btn>
      </template>
    </v-snackbar>
  </v-container>
</template>
<script>
import axios from "axios";
export default {
  data() {
    return {
      projects: [],
      taskCounts: {},
      searchQuery: "",
      dialog: false,
      selectedProject: null,
      projectTasks: [],
      editDialog: false,
      editMode: false,
      deleteDialog: false,
      projectToDelete: null,
      projectForm: { id: null, name: "", description: "" },
      snackbar: { show: false, text: "", color: "success" },
      taskHeaders: [
        { text: "Task Title", value: "title" },
        { text: "Assignee", value: "assigneeName" },
        { text: "Status", value: "status" },
        { text: "Priority", value: "priority" },
        { text: "Created At", value: "createdAt" },
        { text: "Due Date", value: "dueDate" },
      ],
      userRole: "",
    };
  },
  computed: {
    isAdmin() {
      return this.userRole === "ADMIN";
    },
    filteredProjects() {
      if (!this.searchQuery) return this.projects;
      const query = this.searchQuery.toLowerCase();
      return this.projects.filter((p) => p.name.toLowerCase().includes(query));
    },
  },
  async mounted() {
    this.getUserRole();
    await this.fetchProjects();
  },
  methods: {
    getProjectProgress(project) {
      const totalTasks = this.taskCounts[project.id] || 0;
      if (totalTasks === 0) return 0;
      const progress = Math.min((totalTasks / 10) * 100, 100);
      return Math.round(progress);
    },
    getProjectMembers(project) {
      const dummyMembers = [
        {
          name: "Alice",
          avatar: "https://randomuser.me/api/portraits/women/44.jpg",
        },
        {
          name: "Bob",
          avatar: "https://randomuser.me/api/portraits/men/32.jpg",
        },
        {
          name: "Carol",
          avatar: "https://randomuser.me/api/portraits/women/12.jpg",
        },
      ];
      return (
        project.members ||
        dummyMembers.slice(0, Math.floor(Math.random() * 3) + 1)
      );
    },
    showSnackbar(text, color = "success") {
      this.snackbar = { show: true, text, color };
    },
    getUserRole() {
      try {
        const profile = JSON.parse(localStorage.getItem("profile"));
        this.userRole = profile?.role || "";
      } catch {
        this.userRole = "";
      }
    },
    async fetchProjects() {
      try {
        const token = localStorage.getItem("token");
        const { data } = await axios.get("http://localhost:8080/api/projects", {
          headers: { Authorization: `Bearer ${token}` },
        });
        this.projects = data;
        const counts = await Promise.all(
          data.map(async (project) => {
            const count = await this.fetchTaskCount(project.id);
            return { id: project.id, count };
          })
        );
        counts.forEach(({ id, count }) => {
          this.$set(this.taskCounts, id, count);
        });
      } catch (err) {
        console.error("Error fetching projects", err);
      }
    },
    async fetchTaskCount(projectId) {
      try {
        const token = localStorage.getItem("token");
        const { data } = await axios.get(
          `http://localhost:8080/api/tasks/project/${projectId}`,
          { headers: { Authorization: `Bearer ${token}` } }
        );
        return data?.length || 0;
      } catch {
        return 0;
      }
    },
    async openProject(project) {
      this.selectedProject = project;
      this.projectTasks = [];
      this.dialog = true;
      try {
        const token = localStorage.getItem("token");
        const { data } = await axios.get(
          `http://localhost:8080/api/tasks/project/${project.id}`,
          { headers: { Authorization: `Bearer ${token}` } }
        );
        this.projectTasks = data || [];
        this.$set(this.taskCounts, project.id, this.projectTasks.length);
      } catch (err) {
        console.error("Error fetching project tasks", err);
        this.projectTasks = [];
      }
    },
    openCreateDialog() {
      this.editMode = false;
      this.projectForm = { name: "", description: "" };
      this.editDialog = true;
    },
    openEditDialog(project) {
      this.editMode = true;
      this.projectForm = { ...project };
      this.editDialog = true;
    },
    async saveProject() {
      const token = localStorage.getItem("token");
      try {
        if (this.editMode) {
          await axios.put(
            `http://localhost:8080/api/projects/${this.projectForm.id}`,
            this.projectForm,
            { headers: { Authorization: `Bearer ${token}` } }
          );
          this.showSnackbar("Project updated successfully!", "success");
        } else {
          await axios.post(
            "http://localhost:8080/api/projects",
            this.projectForm,
            {
              headers: { Authorization: `Bearer ${token}` },
            }
          );
          this.showSnackbar("Project created successfully!", "success");
        }
        this.editDialog = false;
        await this.fetchProjects();
      } catch (err) {
        console.error("Error saving project", err);
        this.showSnackbar("Failed to save project!", "error");
      }
    },
    confirmDelete(project) {
      this.projectToDelete = project;
      this.deleteDialog = true;
    },
    async deleteProject(id) {
      const token = localStorage.getItem("token");
      try {
        await axios.delete(`http://localhost:8080/api/projects/${id}`, {
          headers: { Authorization: `Bearer ${token}` },
        });
        this.showSnackbar("Project deleted successfully!", "success");
        this.deleteDialog = false;
        await this.fetchProjects();
      } catch (err) {
        console.error("Error deleting project", err);
        this.showSnackbar("Failed to delete project!", "error");
      }
    },
    getStatusColor(status) {
      const map = {
        COMPLETED: "green",
        "In PROGRESS": "orange",
        PENDING: "grey",
      };
      return map[status] || "blue";
    },
    getPriorityColor(priority) {
      const map = { HIGH: "red", MEDIUM: "amber", LOW: "blue" };
      return map[priority] || "grey";
    },
  },
};
</script>
<style scoped>
.modern-container {
  background-color: var(--v-theme-background);
  color: var(--v-theme-on-background);
  min-height: 100vh;
  transition: background-color 0.3s ease, color 0.3s ease;
}
.project-card {
  background-color: var(--v-theme-surface);
  color: var(--v-theme-on-surface);
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  min-height: 280px;
  height: 100%;
  border-radius: 18px;
  overflow: hidden;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.08);
  transition: all 0.25s ease;
}
.project-card:hover {
  transform: translateY(-6px);
  box-shadow: 0 8px 18px rgba(0, 0, 0, 0.15);
}
.card-header {
  background: linear-gradient(135deg, #1976d2, #42a5f5);
}
.theme--dark .card-header {
  background: linear-gradient(135deg, #1565c0, #0d47a1);
}
.transition-ease {
  transition: all 0.25s ease-in-out;
}
.description-wrap {
  white-space: normal !important;
  overflow-wrap: break-word;
  word-break: break-word;
  display: -webkit-box;
  -webkit-line-clamp: 3;
  -webkit-box-orient: vertical;
  overflow: hidden;
}
.search-field {
  width: 320px;
  min-width: 250px;
}
.fade-enter-active,
.fade-leave-active {
  transition: all 0.25s ease;
}
.fade-enter-from,
.fade-leave-to {
  opacity: 0;
  transform: scale(0.98);
}
/* Grid layout */
.project-grid {
  display: flex;
  flex-wrap: wrap;
  align-items: stretch;
}
.project-grid-inner {
  display: flex;
  flex-wrap: wrap;
  width: 100%;
}
.v-col {
  display: flex;
}
.theme--dark .project-card {
  background-color: #1e2a38 !important;
  color: #e0e0e0 !important;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.5);
}
.theme--dark .v-card {
  background-color: #1e2a38 !important;
  color: #e0e0e0 !important;
}
.theme--dark .v-data-table {
  background-color: #243447 !important;
  color: #e0e0e0 !important;
}

.theme--dark .v-dialog {
  background-color: #1f2a35 !important;
}
.theme--dark .v-btn {
  color: #e0e0e0 !important;
}
.theme--dark .v-text-field,
.theme--dark .v-textarea {
  background-color: #2c3e50 !important;
  color: #fff !important;
}
.theme--dark .v-chip {
  color: #fff !important;
}
.theme--dark .v-divider {
  background-color: #34495e !important;
}
</style>
