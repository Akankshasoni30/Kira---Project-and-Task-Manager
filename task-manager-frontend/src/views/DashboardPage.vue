<template>
  <v-container fluid>
    <div v-if="profile.role === 'ADMIN'">
      <v-row class="mb-3" dense>
        <v-col cols="12">
          <div class="filter-bar">
            <v-chip
              v-for="status in statusOptions"
              :key="status"
              outlined
              class="mr-2 mb-2"
              :color="selectedStatus === status ? 'primary' : undefined"
              @click="selectedStatus = status"
            >
              {{ status }}
            </v-chip>
            <v-chip
              v-for="priority in priorityOptions"
              :key="priority"
              outlined
              class="mr-2 mb-2"
              :color="selectedPriority === priority ? 'primary' : undefined"
              @click="selectedPriority = priority"
            >
              {{ priority }}
            </v-chip>
          </div>
        </v-col>
      </v-row>
      <v-row dense class="mb-4">
        <v-col
          v-for="(card, index) in summaryCards"
          :key="index"
          cols="6"
          sm="4"
          md="3"
          lg="2"
        >
          <v-card
            class="summary-mini-card d-flex align-center pa-2"
            elevation="2"
          >
            <div
              :style="{ backgroundColor: card.color }"
              class="accent-bar"
            ></div>
            <v-icon size="24" class="ml-2">{{ card.icon }}</v-icon>
            <div class="ml-2">
              <div class="text-subtitle-2 font-weight-bold">
                {{ card.value }}
              </div>
              <div class="text-caption">{{ card.title }}</div>
            </div>
          </v-card>
        </v-col>
      </v-row>
      <v-row dense>
        <v-col cols="12">
          <v-card class="rounded-card pa-3 mb-3" elevation="2">
            <h4 class="mb-2">Overall Project Task Status</h4>
            <apex-chart
              type="bar"
              height="220"
              :options="chartOptions"
              :series="chartSeries"
            />
          </v-card>
        </v-col>
      </v-row>
      <v-row dense>
        <v-col
          v-for="(p, index) in filteredProjects"
          :key="index"
          cols="12"
          sm="6"
          md="4"
        >
          <v-card class="rounded-card pa-2 project-card mb-3" elevation="2">
            <h5 class="text-subtitle-1 mb-1">{{ p.projectName }}</h5>
            <apex-chart
              type="pie"
              height="180"
              :options="getPieOptions(p)"
              :series="[p.completed, p.inProgress, p.newTasks]"
            />
          </v-card>
        </v-col>
      </v-row>
    </div>
    <div v-else>
      <v-row dense>
        <v-col cols="12" md="6">
          <v-card class="rounded-card pa-3 mb-3" elevation="2">
            <h4 class="mb-2">My Tasks - Progress Overview</h4>
            <apex-chart
              type="bar"
              height="220"
              :options="userBarOptions"
              :series="userBarSeries"
            />
          </v-card>
        </v-col>
        <v-col cols="12" md="6">
          <v-card class="rounded-card pa-3 mb-3" elevation="2">
            <h4 class="mb-2">My Tasks - Priority Distribution</h4>
            <apex-chart
              type="pie"
              height="220"
              :options="userPieOptions"
              :series="userPieSeries"
            />
          </v-card>
        </v-col>
      </v-row>
      <v-row dense>
        <v-col cols="12">
          <v-card class="rounded-card pa-2 mb-3" elevation="2">
            <h4 class="mb-2">My Tasks</h4>
            <v-simple-table dense>
              <thead>
                <tr>
                  <th>Task ID</th>
                  <th>Title</th>
                  <th>Project</th>
                  <th>Status</th>
                  <th>Priority</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="task in tasks" :key="task.taskId">
                  <td>{{ task.taskId }}</td>
                  <td>{{ task.title }}</td>
                  <td>{{ task.projectName }}</td>
                  <td>
                    <v-chip :color="statusColor(task.status)" small dark>{{
                      task.status
                    }}</v-chip>
                  </td>
                  <td>
                    <v-chip :color="priorityColor(task.priority)" small dark>{{
                      task.priority
                    }}</v-chip>
                  </td>
                </tr>
              </tbody>
            </v-simple-table>
          </v-card>
        </v-col>
      </v-row>
    </div>
  </v-container>
</template>
<script>
import axios from "axios";
export default {
  data() {
    return {
      profile: { name: "", email: "", role: "USER" },
      summaryCards: [
        { title: "Projects", value: 0, icon: "mdi-folder", color: "#0052CC" },
        {
          title: "Tasks",
          value: 0,
          icon: "mdi-format-list-bulleted",
          color: "#0052CC",
        },
      ],
      chartOptions: {
        chart: { id: "project-status" },
        xaxis: { categories: [] },
      },
      chartSeries: [],
      projectStats: [],
      tasks: [],
      userBarSeries: [],
      userBarOptions: {
        chart: {
          id: "user-task-status",
          toolbar: { show: false },
        },
        xaxis: {
          categories: ["Completed", "In Progress", "New"],
          labels: {
            style: {
              colors: ["#36B37E", "#FFAB00", "#0052CC"],
              fontSize: "9px",
              fontWeight: 600,
            },
          },
        },
        colors: ["#36B37E", "#FFAB00", "#0052CC"],
        plotOptions: {
          bar: {
            distributed: true,
            borderRadius: 6,
            columnWidth: "50%",
          },
        },
        dataLabels: {
          enabled: true,
          style: {
            colors: ["#fff"],
            fontSize: "12px",
            fontWeight: 500,
          },
        },
        tooltip: {
          theme: this.$vuetify.theme.dark ? "dark" : "light",
        },
        grid: {
          borderColor: this.$vuetify.theme.dark ? "#333" : "#E0E0E0",
        },
        legend: { show: false },
        yaxis: {
          labels: {
            style: {
              colors: this.$vuetify.theme.dark ? "#E0E0E0" : "#212121",
            },
          },
        },
      },

      userPieSeries: [],
      userPieOptions: {
        labels: ["High", "Medium", "Low"],
        colors: ["#FF4560", "#FEB019", "#00E396"],
        legend: { position: "bottom" },
      },

      selectedStatus: "ALL",
      selectedPriority: "ALL",
      statusOptions: ["ALL", "Completed", "In Progress", "New"],
      priorityOptions: ["ALL", "HIGH", "MEDIUM", "LOW"],
    };
  },
  mounted() {
    this.loadProfile();
    this.applyChartThemes();
    this.fetchSummary();
    if (this.profile.role === "ADMIN") {
      this.fetchProjectStats();
    } else {
      this.fetchMyTasks();
    }
  },
  computed: {
    filteredProjects() {
      let result = this.projectStats;
      if (this.selectedStatus !== "ALL") {
        result = result.filter(
          (p) =>
            (this.selectedStatus === "Completed" && p.completed > 0) ||
            (this.selectedStatus === "In Progress" && p.inProgress > 0) ||
            (this.selectedStatus === "New" && p.pending > 0) // Pending → New
        );
      }
      if (this.selectedPriority !== "ALL") {
        result = result.filter((p) => p.priority === this.selectedPriority);
      }
      return result;
    },
  },
  watch: {
    "$vuetify.theme.dark"() {
      this.applyChartThemes();
    },
  },
  methods: {
    applyChartThemes() {
      const dark = this.$vuetify.theme.dark;
      const textColor = dark ? "#E0E0E0" : "#212121";
      const bgColor = dark ? "#1E1E1E" : "#FFFFFF";
      const gridColor = dark ? "#333" : "#E0E0E0";

      this.chartOptions = {
        chart: { background: bgColor, foreColor: textColor },
        xaxis: { labels: { style: { colors: textColor } } },
        yaxis: { labels: { style: { colors: textColor } } },
        grid: { borderColor: gridColor },
        legend: { labels: { colors: textColor } },
        tooltip: { theme: dark ? "dark" : "light" },
        stroke: {
          width: 0,
        },
      };
      this.userBarOptions = this.chartOptions;
      this.userPieOptions = this.chartOptions;
    },
    loadProfile() {
      const stored = localStorage.getItem("profile");
      if (stored) {
        try {
          this.profile = JSON.parse(stored);
        } catch (err) {
          console.error(err);
        }
      }
    },
    async fetchSummary() {
      try {
        const token = localStorage.getItem("token");
        const { data } = await axios.get(
          "http://localhost:8080/api/dashboard/summary",
          { headers: { Authorization: `Bearer ${token}` } }
        );
        this.summaryCards[0].value = data.totalProjects;
        this.summaryCards[1].value = data.totalTasks;
        this.summaryCards[2].value = data.completedTasks;
        this.summaryCards[3].value = data.inProgressTasks;
        this.summaryCards[4].value = data.pendingTasks; // Treat as New
      } catch (err) {
        console.error(err);
      }
    },
    async fetchProjectStats() {
      try {
        const token = localStorage.getItem("token");
        const { data } = await axios.get(
          "http://localhost:8080/api/dashboard/project-stats",
          { headers: { Authorization: `Bearer ${token}` } }
        );

        this.projectStats = data.map((p) => ({
          ...p,
          newTasks: p.pending,
        }));

        const totalCompleted = this.projectStats.reduce(
          (a, p) => a + p.completed,
          0
        );
        const totalInProgress = this.projectStats.reduce(
          (a, p) => a + p.inProgress,
          0
        );
        const totalNew = this.projectStats.reduce((a, p) => a + p.newTasks, 0);
        this.chartSeries = [
          {
            name: "Tasks",
            data: [totalCompleted, totalInProgress, totalNew],
          },
        ];

        this.chartOptions = {
          chart: {
            id: "overall-project-status",
            toolbar: { show: false },
          },
          xaxis: {
            categories: ["Completed", "In Progress", "New"],
            labels: {
              style: {
                colors: ["#36B37E", "#FFAB00", "#0052CC"],
                fontSize: "12px",
                fontWeight: 600,
              },
            },
          },
          colors: ["#36B37E", "#FFAB00", "#0052CC"],
          plotOptions: {
            bar: {
              distributed: true,
              borderRadius: 6,
              columnWidth: "50%",
            },
          },
          dataLabels: {
            enabled: true,
            style: {
              colors: ["#fff"],
              fontSize: "12px",
              fontWeight: 500,
            },
          },
          grid: {
            borderColor: this.$vuetify.theme.dark ? "#333" : "#E0E0E0",
          },
          tooltip: {
            theme: this.$vuetify.theme.dark ? "dark" : "light",
          },
          yaxis: {
            title: {
              text: "Total Tasks",
              style: {
                fontSize: "13px",
                fontWeight: 600,
              },
            },
            labels: {
              style: {
                colors: this.$vuetify.theme.dark ? "#E0E0E0" : "#212121",
              },
            },
          },
          legend: { show: false },
        };
      } catch (err) {
        console.error(err);
      }
    },
    async fetchMyTasks() {
      try {
        const token = localStorage.getItem("token");
        const { data } = await axios.get(
          "http://localhost:8080/api/dashboard/my-tasks",
          { headers: { Authorization: `Bearer ${token}` } }
        );
        this.tasks = data.map((t) => ({
          ...t,
          status: t.status === "PENDING" ? "New" : t.status,
          priority: ["HIGH", "MEDIUM", "LOW"][Math.floor(Math.random() * 3)],
        }));
        const statusCounts = { Completed: 0, "In Progress": 0, New: 0 };
        this.tasks.forEach((t) => {
          statusCounts[t.status] = (statusCounts[t.status] || 0) + 1;
        });
        this.userBarSeries = [
          { name: "Tasks", data: Object.values(statusCounts) },
        ];
        const priorityCounts = { HIGH: 0, MEDIUM: 0, LOW: 0 };
        this.tasks.forEach((t) => {
          priorityCounts[t.priority] = (priorityCounts[t.priority] || 0) + 1;
        });
        this.userPieSeries = Object.values(priorityCounts);
      } catch (err) {
        console.error(err);
      }
    },
    getPieOptions() {
      const dark = this.$vuetify.theme.dark;
      const textColor = dark ? "#E0E0E0" : "#212121";
      return {
        labels: ["Completed", "In Progress", "New"],
        legend: {
          labels: { colors: textColor },
          position: "bottom",
        },
        stroke: {
          width: 0,
        },
        tooltip: { theme: dark ? "dark" : "light" },
      };
    },
    statusColor(s) {
      if (s === "COMPLETED") return "#36B37E";
      if (s === "IN PROGRESS") return "#FFAB00";
      if (s === "NEW") return "#0052CC";
      return "grey";
    },
    priorityColor(p) {
      if (p === "HIGH") return "#FF4560";
      if (p === "MEDIUM") return "#FEB019";
      if (p === "LOW") return "#00E396";
      return "grey";
    },
  },
};
</script>
<style scoped>
.summary-mini-card {
  border-radius: 12px;
  display: flex;
  align-items: center;
  min-height: 60px;
  text-align: left;
  cursor: default;
  transition: transform 0.2s, background-color 0.3s;
  background-color: rgb(var(--v-theme-surface));
  color: rgb(var(--v-theme-on-surface));
}
.summary-mini-card:hover {
  transform: translateY(-2px);
}
.accent-bar {
  width: 4px;
  height: 100%;
  border-radius: 2px;
}
.rounded-card {
  border-radius: 16px;
  background-color: rgb(var(--v-theme-surface));
  color: rgb(var(--v-theme-on-surface));
  transition: box-shadow 0.3s, background-color 0.3s;
  margin-bottom: 12px;
}
.rounded-card:hover {
  box-shadow: 0 10px 25px rgba(0, 0, 0, 0.15);
}
.project-card {
  display: flex;
  flex-direction: column;
  align-items: center;
}
.filter-bar {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  background-color: rgb(var(--v-theme-surface-variant));
  color: rgb(var(--v-theme-on-surface-variant));
  padding: 6px 12px;
  border-radius: 8px;
  transition: background-color 0.3s;
}
.filter-bar .v-chip {
  cursor: pointer;
  transition: all 0.2s;
}
.filter-bar .v-chip:hover {
  background-color: rgb(var(--v-theme-secondary-container));
}
h4,
h5 {
  font-weight: 500;
  margin-bottom: 0.5rem;
  color: rgb(var(--v-theme-on-surface));
}
</style>
