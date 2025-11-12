<template>
  <v-container fluid class="login-page fill-height pa-0">
    <v-row no-gutters class="fill-height">
      <v-col
        cols="12"
        md="6"
        class="login-left d-flex flex-column justify-center align-center text-center"
      >
        <v-img
          src="@/assets/kira.png"
          max-width="600"
          contain
          class="logo-image"
        ></v-img>
      </v-col>
      <v-col
        cols="12"
        md="6"
        class="login-right d-flex justify-center align-center"
      >
        <v-card elevation="10" class="pa-8 login-card">
          <div class="text-center mb-6">
            <v-img
              src="@/assets/kirasmall.png"
              max-width="60"
              class="mx-auto mb-3"
            ></v-img>
            <h2 class="font-weight-bold text-primary">
              {{
                isForgotPassword
                  ? "Reset Password"
                  : isLogin
                  ? "Welcome Back!"
                  : "Create Account"
              }}
            </h2>
            <p class="grey--text text--darken-1">
              {{
                isForgotPassword
                  ? "Enter your email and new password"
                  : isLogin
                  ? "Login to continue to Kira"
                  : "Join Kira and start organizing your tasks!"
              }}
            </p>
          </div>
          <v-form ref="form" v-model="valid" lazy-validation>
            <v-text-field
              v-if="!isLogin && !isForgotPassword"
              v-model="formData.name"
              label="Full Name"
              outlined
              dense
              :rules="[(v) => !!v || 'Name is required']"
            ></v-text-field>
            <v-text-field
              v-model="formData.email"
              label="Email"
              outlined
              dense
              :rules="[
                (v) => !!v || 'Email is required',
                (v) => /.+@.+\..+/.test(v) || 'Enter a valid email',
              ]"
            ></v-text-field>
            <v-text-field
              v-if="!isForgotPassword || (isForgotPassword && formData.email)"
              v-model="formData.password"
              label="Password"
              :type="showPassword ? 'text' : 'password'"
              outlined
              dense
              :append-icon="showPassword ? 'mdi-eye' : 'mdi-eye-off'"
              @click:append="showPassword = !showPassword"
              :rules="[(v) => !!v || 'Password is required']"
            ></v-text-field>
            <v-btn
              color="#0052cc"
              dark
              block
              large
              class="mt-4 mb-3 login-btn"
              @click="submitForm"
            >
              {{
                isForgotPassword
                  ? "Reset Password"
                  : isLogin
                  ? "Login"
                  : "Register"
              }}
            </v-btn>
            <div class="text-center">
              <v-btn
                v-if="!isForgotPassword"
                text
                small
                color="primary"
                @click="toggleMode"
              >
                {{ isLogin ? "Create an account" : "Back to Login" }}
              </v-btn>
              <v-btn
                v-if="isLogin && !isForgotPassword"
                text
                small
                color="secondary"
                @click="isForgotPassword = true"
              >
                Forgot Password?
              </v-btn>
              <v-btn
                v-if="isForgotPassword"
                text
                small
                color="primary"
                @click="isForgotPassword = false"
              >
                Back to Login
              </v-btn>
            </div>
          </v-form>
        </v-card>
        <v-snackbar
          v-model="snackbar.show"
          :color="snackbar.color"
          timeout="3000"
          top
          right
          elevation="3"
        >
          {{ snackbar.message }}
        </v-snackbar>
        <template v-slot:action="{ attrs }">
          <v-btn text v-bind="attrs" @click="snackbar.show = false">
            <v-icon>mdi-close</v-icon>
          </v-btn>
        </template>
      </v-col>
    </v-row>
  </v-container>
</template>
<script>
import axios from "axios";
export default {
  name: "LoginRegister",
  data() {
    return {
      valid: false,
      showPassword: false,
      isLogin: true,
      isForgotPassword: false,
      formData: {
        name: "",
        email: "",
        password: "",
        role: "USER",
      },
      snackbar: {
        show: false,
        message: "",
        color: "success",
      },
    };
  },
  methods: {
    toggleMode() {
      this.isLogin = !this.isLogin;
      this.isForgotPassword = false;
      this.formData = { name: "", email: "", password: "", role: "USER" };
    },
    showSnackbar(message, color = "success") {
      this.snackbar.message = message;
      this.snackbar.color = color;
      this.snackbar.show = true;
    },
    async submitForm() {
      if (!this.$refs.form.validate()) return;

      try {
        if (this.isForgotPassword) {
          await axios.post("http://localhost:8080/api/auth/reset-password", {
            email: this.formData.email,
            newPassword: this.formData.password,
          });
          this.showSnackbar("Password reset successful!", "success");
          this.isForgotPassword = false;
          this.formData.password = "";
        } else if (this.isLogin) {
          const res = await axios.post("http://localhost:8080/api/auth/login", {
            email: this.formData.email,
            password: this.formData.password,
          });

          localStorage.setItem("token", res.data.token);
          localStorage.setItem(
            "profile",
            JSON.stringify({
              name: res.data.name,
              email: res.data.email,
              role: res.data.role,
              id: res.data.id,
              status: "online",
            })
          );

          this.showSnackbar("Login successful! Redirecting...", "success");
          setTimeout(() => this.$router.push("/layout"), 1000);
        } else {
          await axios.post("http://localhost:8080/api/auth/register", {
            name: this.formData.name,
            email: this.formData.email,
            password: this.formData.password,
            role: this.formData.role,
          });
          this.showSnackbar(
            "Registration successful! Please login.",
            "success"
          );
          this.toggleMode();
        }
      } catch (err) {
        this.showSnackbar(
          err.response?.data?.message || "Please check your Credentials",
          "error"
        );
      }
    },
  },
};
</script>
<style scoped>
.login-page {
  min-height: 100vh;
  background: linear-gradient(135deg, #0052cc 0%, #1d2b64 100%);
  overflow: hidden;
}
.v-snackbar {
  border-radius: 8px;
  font-weight: 500;
}
.login-left {
  background: linear-gradient(145deg, #0052cc, #2a7de1);
  color: white;
  padding: 60px 30px;
}
.app-title {
  font-size: 2.8rem;
  font-weight: 700;
  letter-spacing: 1px;
  font-family: "Poppins", sans-serif;
}
.login-right {
  background: #f9fafc;
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 40px;
}
.login-card {
  width: 100%;
  max-width: 420px;
  border-radius: 16px;
  background: #fff;
  box-shadow: 0 12px 32px rgba(0, 0, 0, 0.15);
  transition: all 0.3s ease;
}
.login-card:hover {
  box-shadow: 0 16px 36px rgba(0, 0, 0, 0.2);
}
.login-btn {
  border-radius: 30px;
  font-weight: 600;
  text-transform: none;
}
@media (max-width: 960px) {
  .login-left {
    display: none;
  }
  .login-right {
    padding: 24px;
    background: linear-gradient(135deg, #0052cc, #1d2b64);
  }
  .login-card {
    background: rgba(255, 255, 255, 0.95);
    backdrop-filter: blur(10px);
  }
}
.logo-image {
  filter: drop-shadow(0 2px 6px rgba(0, 0, 0, 0.2));
  margin-bottom: 8px !important;
}
.cursive-text {
  font-family: "Dancing Script", "Brush Script MT", cursive;
  font-weight: 500;
  letter-spacing: 0.5px;
}
</style>
