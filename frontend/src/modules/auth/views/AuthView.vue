<template>
  <main class="auth-page">
    <section class="auth-card">
      <div class="auth-copy">
        <p class="eyebrow">Weight Tracker Calendar</p>
        <h1>{{ mode === "login" ? "Accede a tu cuenta" : "Crea tu cuenta" }}</h1>
        <p>
          Accede con tu cuenta para ver tus registros, tu objetivo y tu calendario personal.
        </p>
      </div>

      <div class="auth-toggle">
        <button type="button" :class="{ active: mode === 'login' }" @click="mode = 'login'">Login</button>
        <button type="button" :class="{ active: mode === 'register' }" @click="mode = 'register'">Registro</button>
      </div>

      <p v-if="errorMessage" class="error">{{ errorMessage }}</p>

      <form v-if="mode === 'login'" class="auth-form" @submit.prevent="handleLogin">
        <label>
          <span>Usuario</span>
          <input v-model="loginForm.username" autocomplete="username" required />
        </label>
        <label>
          <span>Contraseña</span>
          <input v-model="loginForm.password" type="password" autocomplete="current-password" required />
        </label>
        <button :disabled="authStore.loading" type="submit">Entrar</button>
      </form>

      <form v-else class="auth-form" @submit.prevent="handleRegister">
        <label>
          <span>Usuario</span>
          <input v-model="registerForm.username" autocomplete="username" required />
        </label>
        <label>
          <span>Nombre</span>
          <input v-model="registerForm.firstName" autocomplete="given-name" required />
        </label>
        <label>
          <span>Apellidos</span>
          <input v-model="registerForm.lastName" autocomplete="family-name" />
        </label>
        <label>
          <span>Email</span>
          <input v-model="registerForm.email" type="email" autocomplete="email" />
        </label>
        <label>
          <span>Contraseña</span>
          <input v-model="registerForm.password" type="password" autocomplete="new-password" required />
        </label>
        <button :disabled="authStore.loading" type="submit">Crear cuenta</button>
      </form>
    </section>
  </main>
</template>

<script setup lang="ts">
import { reactive, ref } from "vue";
import { useAuthStore } from "../store/useAuthStore";

const authStore = useAuthStore();
const mode = ref<"login" | "register">("login");
const errorMessage = ref("");

const loginForm = reactive({
  username: "",
  password: ""
});

const registerForm = reactive({
  username: "",
  firstName: "",
  lastName: "",
  email: "",
  password: ""
});

async function handleLogin() {
  errorMessage.value = "";
  try {
    await authStore.login(loginForm);
  } catch (error) {
    errorMessage.value = error instanceof Error ? error.message : "No se pudo iniciar sesión.";
  }
}

async function handleRegister() {
  errorMessage.value = "";
  try {
    await authStore.register({
      username: registerForm.username,
      displayName: buildDisplayName(registerForm.firstName, registerForm.lastName),
      email: registerForm.email || undefined,
      password: registerForm.password
    });
  } catch (error) {
    errorMessage.value = error instanceof Error ? error.message : "No se pudo crear la cuenta.";
  }
}

function buildDisplayName(firstName: string, lastName: string) {
  const trimmedFirstName = firstName.trim();
  const trimmedLastName = lastName.trim();
  return trimmedLastName ? `${trimmedFirstName} ${trimmedLastName}` : trimmedFirstName;
}
</script>

<style scoped>
.auth-page { min-height: 100vh; display: grid; place-items: center; padding: 24px; }
.auth-card { width: min(100%, 560px); padding: 28px; border-radius: 28px; background: rgba(255,255,255,0.9); border: 1px solid #d9e5f2; box-shadow: 0 20px 50px rgba(19, 34, 56, 0.12); display: grid; gap: 18px; }
.eyebrow { text-transform: uppercase; letter-spacing: 0.12em; font-size: 12px; color: #5f7895; margin: 0; }
.auth-copy h1 { margin: 8px 0 12px; }
.auth-copy p { margin: 0; color: #4d627b; line-height: 1.5; }
.auth-toggle { display: grid; grid-template-columns: repeat(2, minmax(0, 1fr)); gap: 8px; }
.auth-toggle button { border: 1px solid #d3dfec; border-radius: 12px; padding: 10px 12px; background: #fff; cursor: pointer; }
.auth-toggle button.active { background: #132238; color: #f5f9ff; border-color: #132238; }
.auth-form { display: grid; gap: 14px; }
.auth-form label { display: grid; gap: 6px; }
.auth-form input { border: 1px solid #d3dfec; border-radius: 12px; padding: 12px 14px; }
.auth-form button { border: 0; border-radius: 14px; padding: 14px 18px; background: #132238; color: #f5f9ff; font-weight: 700; cursor: pointer; }
.error { margin: 0; padding: 12px 14px; border-radius: 12px; background: #ffe6e2; color: #8a2f25; }
</style>
