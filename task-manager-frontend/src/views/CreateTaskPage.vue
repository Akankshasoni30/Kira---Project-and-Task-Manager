<v-overlay :value="loading" opacity="0.1">
  <v-progress-circular indeterminate color="primary" size="64"></v-progress-circular>
</v-overlay>
<template>
  <v-container fluid class="pa-6 task-board">
    <div>
      <p class="text-subtitle-2 grey--text text--darken-1">
        💡 Drag and drop tasks between columns to update their status instantly.
      </p>
    </div>
    <v-row align="center" justify="space-between" class="mb-4">
      <v-col cols="12" md="6">
        <v-text-field
          v-model="taskFilter"
          placeholder="Search tasks by name..."
          outlined
          dense
          clearable
          rounded
          prepend-inner-icon="mdi-magnify"
          class="search-field"
          hide-details
          color="primary"
        ></v-text-field>
      </v-col>
      <v-col cols="12" md="6" class="d-flex justify-end">
        <v-btn
          color="primary"
          dark
          class="mr-2"
          @click="exportTasks"
          elevation="3"
        >
          <v-icon left>mdi-file-export</v-icon>
          Export
        </v-btn>
        <v-btn
          v-if="isAdmin"
          color="primary"
          dark
          elevation="3"
          @click="openCreateDialog"
        >
          <v-icon left>mdi-plus</v-icon>
          Add Task
        </v-btn>
      </v-col>
    </v-row>
    <v-row dense>
      <v-col
        v-for="status in statuses"
        :key="status"
        cols="12"
        md="4"
        class="kanban-column"
      >
        <v-card class="pa-3 elevation-3 column-card">
          <h3
            class="text-h6 font-weight-bold mb-3 status-heading"
            :style="{
              backgroundColor: getStatusColor(status),
              color: 'white',
              padding: '6px 12px',
              borderRadius: '8px',
            }"
          >
            <v-icon small class="mr-1">{{ statusIcon(status) }}</v-icon>
            {{ statusLabels[status] }}
          </h3>

          <div
            class="drop-zone"
            @dragover.prevent
            @drop="onDrop($event, status)"
          >
            <transition-group name="fade" tag="div">
              <v-card
                v-for="task in filteredTasksByStatus(status)"
                :key="task.id"
                class="mb-3 task-card"
                draggable="true"
                @dragstart="onDragStart($event, task)"
              >
                <v-card-title class="text-subtitle-2 justify-space-between">
                  {{ task.title }}
                  <v-chip :color="getPriorityColor(task.priority)" small dark>
                    {{ task.priority }}
                  </v-chip>
                </v-card-title>

                <v-card-text class="text-caption" @click="openTaskDialog(task)">
                  <div>
                    <v-icon small class="mr-1">mdi-account</v-icon>
                    <strong>Assignee:</strong> {{ task.assigneeName }}
                  </div>
                  <div>
                    <v-icon small class="mr-1">mdi-folder</v-icon>
                    <strong>Project:</strong> {{ task.projectName }}
                  </div>
                </v-card-text>

                <v-card-actions
                  class="justify-end px-2 py-1"
                  style="position: absolute; bottom: 5px; right: 5px"
                >
                  <v-btn
                    icon
                    small
                    color="primary"
                    @click.stop="openTaskDialog(task)"
                  >
                    <v-icon small>mdi-eye</v-icon>
                  </v-btn>
                  <v-btn
                    v-if="isAdmin"
                    icon
                    small
                    color="blue"
                    @click.stop="openEditDialog(task)"
                  >
                    <v-icon small>mdi-pencil</v-icon>
                  </v-btn>
                  <v-btn
                    v-if="isAdmin"
                    icon
                    small
                    color="red"
                    @click.stop="deleteTask(task.id)"
                  >
                    <v-icon small>mdi-delete</v-icon>
                  </v-btn>
                </v-card-actions>
              </v-card>
            </transition-group>
          </div>
        </v-card>
      </v-col>
    </v-row>
    <v-dialog v-model="taskDialog" max-width="700px">
      <v-card>
        <v-card-title class="justify-space-between">
          <span class="font-weight-bold">{{ selectedTask?.title }}</span>
          <div class="d-flex">
            <v-btn icon small @click="taskDialog = false">
              <v-icon small>mdi-close</v-icon>
            </v-btn>
          </div>
        </v-card-title>
        <v-divider></v-divider>
        <v-card-text>
          <v-row dense>
            <v-col cols="12">
              <p>
                <strong>Description:</strong> {{ selectedTask?.description }}
              </p>
            </v-col>
            <v-col cols="6"
              ><p><strong>Status:</strong> {{ selectedTask?.status }}</p></v-col
            >
            <v-col cols="6"
              ><p>
                <strong>Priority: </strong
                ><v-chip
                  :color="getPriorityColor(selectedTask?.priority)"
                  small
                  dark
                  >{{ selectedTask?.priority }}</v-chip
                >
              </p></v-col
            >
            <v-col cols="6"
              ><p>
                <strong>Project:</strong> {{ selectedTask?.projectName }}
              </p></v-col
            >
            <v-col cols="6"
              ><p>
                <strong>Assignee:</strong> {{ selectedTask?.assigneeName }}
              </p></v-col
            >
            <v-col cols="6">
              <p>
                <strong>Due Date:</strong>
                {{
                  selectedTask?.dueDate
                    ? new Date(selectedTask.dueDate).toLocaleDateString()
                    : "—"
                }}
              </p>
            </v-col>
          </v-row>
          <v-divider class="my-3"></v-divider>
          <h4 class="text-subtitle-1 mb-2">Comments</h4>
          <v-list dense>
            <v-list-item v-for="(comment, i) in comments" :key="i">
              <v-list-item-content>
                <v-list-item-title>
                  <v-icon small class="mr-1">mdi-comment-text</v-icon>
                  {{ comment.text }}
                </v-list-item-title>
                <v-list-item-subtitle class="text-caption">
                  {{ new Date(comment.createdAt).toLocaleString() }}
                </v-list-item-subtitle>
              </v-list-item-content>
            </v-list-item>
          </v-list>
          <v-text-field
            v-model="newComment"
            label="Add a comment"
            dense
            outlined
            class="mt-2"
            prepend-inner-icon="mdi-comment-plus"
          ></v-text-field>
          <v-btn small color="primary" @click="postComment">
            <v-icon left>mdi-plus</v-icon> Post
          </v-btn>
          <v-divider class="my-3"></v-divider>
          <h4 class="text-subtitle-1 mb-2">Attachments</h4>
          <v-list dense>
            <v-list-item v-for="(file, i) in attachments" :key="i">
              <v-icon small class="mr-1">mdi-paperclip</v-icon>
              <v-list-item-title>{{ file.fileName }}</v-list-item-title>
            </v-list-item>
          </v-list>
          <v-text-field
            v-model="newAttachment.fileName"
            label="Attachment Name"
            dense
            outlined
            prepend-inner-icon="mdi-plus-box"
          ></v-text-field>
          <v-btn small color="primary" @click="addAttachment">
            <v-icon left>mdi-plus</v-icon> Add Attachment
          </v-btn>
        </v-card-text>
      </v-card>
    </v-dialog>
    <v-dialog v-if="isAdmin" v-model="editDialog" max-width="600px">
      <v-card>
        <v-card-title>
          {{ editMode ? "Edit Task" : "Create Task" }}
        </v-card-title>
        <v-card-text>
          <v-text-field v-model="taskForm.title" label="Title" outlined dense />
          <v-textarea
            v-model="taskForm.description"
            label="Description"
            outlined
            dense
          />
          <v-select
            v-model="taskForm.priority"
            :items="['LOW', 'MEDIUM', 'HIGH']"
            label="Priority"
            outlined
            dense
          />
          <v-select
            v-model="taskForm.status"
            :items="['NEW', 'In PROGRESS', 'COMPLETED']"
            label="Status"
            outlined
            dense
          />
          <v-select
            v-if="!editMode"
            v-model="taskForm.project.id"
            :items="projects"
            item-text="name"
            item-value="id"
            label="Select Project"
            outlined
            dense
          />

          <v-select
            v-if="!editMode"
            v-model="taskForm.assignee.id"
            :items="users"
            item-text="name"
            item-value="id"
            label="Assign User"
            outlined
            dense
          />
          <v-text-field
            v-model="taskForm.dueDate"
            label="Due Date"
            type="date"
            outlined
            dense
            :rules="[(v) => !!v || 'Due date is required']"
            required
          />
        </v-card-text>
        <v-card-actions>
          <v-spacer></v-spacer>
          <v-btn text @click="editDialog = false" :disabled="loading">Cancel</v-btn>
          <v-btn color="primary" @click="saveTask" :loading="loading" :disabled="loading">
            {{ editMode ? "Update" : "Create" }}
          </v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
    <v-snackbar
      v-model="snackbar.show"
      :timeout="3000"
      :color="snackbar.color"
      top
      right
      elevation="5"
    >
      {{ snackbar.message }}
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
      tasks: [],
      projects: [],
      users: [],
      comments: [],
      attachments: [],
      selectedTask: null,
      draggedTask: null,
      editDialog: false,
      taskDialog: false,
      editMode: false,
      newComment: "",
      newAttachment: { fileName: "" },
      userRole: "",
      taskForm: {
        title: "",
        description: "",
        priority: "",
        status: "NEW",
        project: { id: null },
        assignee: { id: null },
        dueDate: "",
      },
      statuses: ["NEW", "In PROGRESS", "COMPLETED"],
      statusLabels: {
        NEW: "New Tasks",
        "In PROGRESS": "In Progress",
        COMPLETED: "Completed",
      },
      taskFilter: "",
      snackbar: {
        show: false,
        message: "",
        color: "success",
      },
      loading: false,
    };
  },
  computed: {
    isAdmin() {
      return this.userRole === "ADMIN";
    },
  },
  async mounted() {
    this.getUserRole();
    await this.fetchTasks();
    await this.fetchProjects();
    await this.fetchUsers();
  },
  methods: {
    showSnackbar(message, color = "success") {
      this.snackbar.message = message;
      this.snackbar.color = color;
      this.snackbar.show = true;
    },
    getStatusColor(status) {
      switch (status) {
        case "NEW":
          return "#42a5f5";
        case "In PROGRESS":
          return "#42a5f5";
        case "COMPLETED":
          return "#42a5f5";
        default:
          return "#757575";
      }
    },
    getUserRole() {
      const profile = JSON.parse(localStorage.getItem("profile"));
      this.userRole = profile?.role || "";
    },
    statusIcon(status) {
      switch (status?.toUpperCase()) {
        case "NEW":
          return "mdi-star-circle-outline";
        case "IN PROGRESS":
          return "mdi-sync-circle";
        case "COMPLETED":
          return "mdi-check-circle";
        default:
          return "mdi-file-outline";
      }
    },
    getPriorityColor(priority) {
      return { HIGH: "red", MEDIUM: "orange", LOW: "blue" }[priority] || "grey";
    },
    filteredTasksByStatus(status) {
      return this.tasks
        .filter((t) => t.status === status)
        .filter((t) =>
          t.title.toLowerCase().includes(this.taskFilter.toLowerCase())
        );
    },
    async fetchTasks() {
      const token = localStorage.getItem("token");
      const { data } = await axios.get("http://localhost:8080/api/tasks", {
        headers: { Authorization: `Bearer ${token}` },
      });
      this.tasks = this.isAdmin
        ? data
        : data.filter(
            (t) =>
              t.assigneeEmail ===
              JSON.parse(localStorage.getItem("profile"))?.email
          );
    },
    async fetchProjects() {
      const token = localStorage.getItem("token");
      const { data } = await axios.get("http://localhost:8080/api/projects", {
        headers: { Authorization: `Bearer ${token}` },
      });
      this.projects = data;
    },
    async fetchUsers() {
      const token = localStorage.getItem("token");
      const { data } = await axios.get("http://localhost:8080/api/auth/users", {
        headers: { Authorization: `Bearer ${token}` },
      });
      this.users = data;
    },
    onDragStart(e, task) {
      this.draggedTask = task;
    },
    async onDrop(e, newStatus) {
      if (!this.draggedTask) return;
      const token = localStorage.getItem("token");
      await axios.patch(
        `http://localhost:8080/api/tasks/${this.draggedTask.id}/status`,
        { status: newStatus },
        { headers: { Authorization: `Bearer ${token}` } }
      );

      this.draggedTask.status = newStatus;
      this.draggedTask = null;
      await this.fetchTasks();
      this.showSnackbar("Task status updated!", "info");
    },
    openTaskDialog(task) {
      this.selectedTask = task;
      this.taskDialog = true;
      this.loadTaskDetails(task.id);
    },
    async loadTaskDetails(taskId) {
      const token = localStorage.getItem("token");
      const commentsRes = await axios.get(
        `http://localhost:8080/api/comments/task/${taskId}`,
        { headers: { Authorization: `Bearer ${token}` } }
      );
      this.comments = commentsRes.data || [];

      const attachRes = await axios.get(
        `http://localhost:8080/api/attachments/task/${taskId}`,
        { headers: { Authorization: `Bearer ${token}` } }
      );
      this.attachments = attachRes.data || [];
    },
    async postComment() {
      if (!this.newComment || !this.selectedTask) return;
      const token = localStorage.getItem("token");
      const profile = JSON.parse(localStorage.getItem("profile"));
      await axios.post(
        "http://localhost:8080/api/comments",
        {
          text: this.newComment,
          task: { id: this.selectedTask.id },
          user: { id: profile.id },
        },
        { headers: { Authorization: `Bearer ${token}` } }
      );
      this.newComment = "";
      await this.loadTaskDetails(this.selectedTask.id);
      this.showSnackbar("Comment added!");
    },
    async addAttachment() {
      if (!this.newAttachment.fileName || !this.selectedTask) return;

      const token = localStorage.getItem("token");
      const profile = JSON.parse(localStorage.getItem("profile"));

      await axios.post(
        "http://localhost:8080/api/attachments",
        {
          fileName: this.newAttachment.fileName,
          task: { id: this.selectedTask.id },
          uploadedBy: { id: profile.id },
        },
        { headers: { Authorization: `Bearer ${token}` } }
      );

      this.newAttachment.fileName = "";
      await this.loadTaskDetails(this.selectedTask.id);
      this.showSnackbar("Attachment added!");
    },
    openCreateDialog() {
      this.editMode = false;
      this.taskForm = {
        title: "",
        description: "",
        priority: "LOW",
        status: "NEW",
        project: { id: null },
        assignee: { id: null },
      };
      this.editDialog = true;
    },
    openEditDialog(task) {
      this.editMode = true;
      this.taskForm = {
        ...task,
        project: { id: task.projectId },
        assignee: { id: task.assigneeId },
      };
      this.editDialog = true;
    },
    async saveTask() {
      if (!this.taskForm.dueDate) {
        this.showSnackbar("Due Date is required");
        return;
      }
      this.loading = true;
      const token = localStorage.getItem("token");
      if (this.editMode) {
        await axios.put(
          `http://localhost:8080/api/tasks/${this.taskForm.id}`,
          this.taskForm,
          { headers: { Authorization: `Bearer ${token}` } }
        );
        this.showSnackbar("Task updated!");
        this.loading = false;
      } else {
        await axios.post("http://localhost:8080/api/tasks", this.taskForm, {
          headers: { Authorization: `Bearer ${token}` },
        });
        this.showSnackbar("Task created!");
        this.loading = false;
      }
      this.editDialog = false;
      await this.fetchTasks();
    },
    async deleteTask(id) {
      const token = localStorage.getItem("token");
      await axios.delete(`http://localhost:8080/api/tasks/${id}`, {
        headers: { Authorization: `Bearer ${token}` },
      });
      this.taskDialog = false;
      await this.fetchTasks();
      this.showSnackbar("Task deleted!", "error");
    },
    exportTasks() {
      const headers = [
        "Title",
        "Description",
        "Status",
        "Priority",
        "Project",
        "Assignee",
      ];
      const rows = this.tasks
        .filter((t) =>
          t.title.toLowerCase().includes(this.taskFilter.toLowerCase())
        )
        .map((t) => [
          t.title,
          t.description,
          t.status,
          t.priority,
          t.projectName,
          t.assigneeName,
        ]);

      let csvContent =
        "data:text/csv;charset=utf-8," +
        headers.join(",") +
        "\n" +
        rows.map((e) => e.map((v) => `"${v}"`).join(",")).join("\n");

      const encodedUri = encodeURI(csvContent);
      const link = document.createElement("a");
      link.setAttribute("href", encodedUri);
      link.setAttribute("download", "tasks_export.csv");
      document.body.appendChild(link);
      link.click();
      document.body.removeChild(link);
      this.showSnackbar("Tasks exported successfully!", "info");
    },
  },
};
</script>
<style scoped>
.search-field {
  background: var(--v-theme-surface);
  color: var(--v-theme-on-surface);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  transition: background 0.3s ease, color 0.3s ease;
}
.task-card {
  transition: transform 0.2s ease, box-shadow 0.2s ease;
  border-radius: 10px;
  cursor: grab;
  background-color: var(--v-theme-surface);
  color: var(--v-theme-on-surface);
}
.task-card:hover {
  transform: scale(1.03);
  box-shadow: 0 6px 20px rgba(0, 0, 0, 0.18);
}
.column-card {
  border-radius: 14px;
  background-color: var(--v-theme-surface);
  color: var(--v-theme-on-surface);
  min-height: 300px;
  transition: background-color 0.3s ease, color 0.3s ease;
}
.drop-zone {
  min-height: 200px;
  transition: background 0.2s ease;
  padding: 5px;
}
.drop-zone:hover {
  background: var(--v-theme-background);
}
.kanban-column h3 {
  display: flex;
  align-items: center;
  font-weight: 600;
}
.theme--dark .task-card,
.theme--dark .column-card {
  background-color: #1e2a38 !important;
  color: #e0e0e0 !important;
  box-shadow: 0 3px 12px rgba(0, 0, 0, 0.5);
}
.theme--dark .drop-zone:hover {
  background-color: #243447 !important;
}
.theme--dark .v-dialog {
  background-color: #1f2a35 !important;
  color: #e0e0e0 !important;
}
.theme--dark .v-list {
  background-color: #1f2a35 !important;
  color: #e0e0e0 !important;
}
.theme--dark .v-text-field,
.theme--dark .v-textarea,
.theme--dark .v-select {
  background-color: #2c3e50 !important;
  color: #fff !important;
}
.theme--dark .v-btn {
  color: #e0e0e0 !important;
}
.theme--dark .v-divider {
  background-color: #34495e !important;
}
.theme--dark .v-chip {
  color: #fff !important;
}
</style>
