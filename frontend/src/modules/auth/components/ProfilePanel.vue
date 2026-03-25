<template>
  <section class="profile-shell">
    <header class="profile-header">
      <div>
        <p class="eyebrow">Tu perfil</p>
        <h1>Datos de usuario</h1>
        <p class="copy">
          Revisa tu usuario, nombre, apellidos y correo sin salir del calendario. Los cambios se guardan
          sobre tu propia cuenta.
        </p>
      </div>
      <div class="header-actions">
        <button v-if="showClose" type="button" class="ghost-button" @click="$emit('close')">Cerrar</button>
        <button type="button" class="ghost-button" @click="authStore.logout()">Salir</button>
      </div>
    </header>

    <section class="profile-layout">
      <section class="card account-overview">
        <p class="eyebrow">Cuenta</p>
        <h2>{{ accountTitle }}</h2>
        <p class="copy">
          Desde aquí puedes mantener al día tus datos personales y la forma en la que se identifica tu cuenta.
        </p>
        <div class="account-chips">
          <span class="account-chip">Zona horaria {{ authStore.user?.timezone ?? "sin definir" }}</span>
          <span class="account-chip">{{ profileForm.email || "Sin correo configurado" }}</span>
        </div>
      </section>

      <section class="card">
        <div class="card-head">
          <div>
            <p class="eyebrow">Perfil</p>
            <h2>Información personal</h2>
          </div>
        </div>

        <p v-if="successMessage" class="success">{{ successMessage }}</p>
        <p v-if="errorMessage" class="error">{{ errorMessage }}</p>

        <form class="profile-form" @submit.prevent="handleProfileSave">
          <label>
            <span>Usuario</span>
            <input v-model="profileForm.username" autocomplete="username" required />
          </label>
          <label>
            <span>Nombre</span>
            <input v-model="profileForm.firstName" autocomplete="given-name" required />
          </label>
          <label>
            <span>Apellidos</span>
            <input v-model="profileForm.lastName" autocomplete="family-name" />
          </label>
          <label>
            <span>Correo</span>
            <input v-model="profileForm.email" type="email" autocomplete="email" />
          </label>
          <button :disabled="authStore.loading" type="submit">Guardar datos</button>
        </form>
      </section>

      <ChangePasswordPanel />
    </section>
  </section>
</template>

<script setup lang="ts">
import { computed, reactive, ref, watch } from "vue";
import ChangePasswordPanel from "./ChangePasswordPanel.vue";
import { useAuthStore } from "../store/useAuthStore";
import { buildProfileFormState, toUpdateProfileInput } from "../utils/profileForm";

defineProps<{
  showClose?: boolean;
}>();

defineEmits<{
  close: [];
}>();

const authStore = useAuthStore();
const successMessage = ref("");
const errorMessage = ref("");
const profileForm = reactive({
  username: "",
  firstName: "",
  lastName: "",
  email: ""
});

const accountTitle = computed(() =>
  [profileForm.firstName, profileForm.lastName].filter(Boolean).join(" ") || profileForm.username || "Tu cuenta"
);

watch(
  () => authStore.user,
  (user) => {
    if (!user) return;
    Object.assign(profileForm, buildProfileFormState(user));
  },
  { immediate: true }
);

async function handleProfileSave() {
  successMessage.value = "";
  errorMessage.value = "";

  try {
    await authStore.updateProfile(toUpdateProfileInput(profileForm));
    successMessage.value = "Tus datos se han actualizado correctamente.";
  } catch (error) {
    errorMessage.value = error instanceof Error ? error.message : "No se pudo actualizar el perfil.";
  }
}
</script>

<style scoped>
.profile-shell {
  display: grid;
  gap: 24px;
}

.profile-layout {
  display: grid;
  gap: 24px;
}

.profile-header,
.card,
:deep(.password-card) {
  padding: 24px;
  border-radius: 24px;
  background: rgba(255, 255, 255, 0.97);
  border: 1px solid #d9e5f2;
  box-shadow: 0 20px 50px rgba(19, 34, 56, 0.08);
}

.profile-header {
  display: flex;
  justify-content: space-between;
  gap: 16px;
  align-items: flex-start;
}

.header-actions {
  display: flex;
  gap: 10px;
}

.eyebrow {
  margin: 0;
  text-transform: uppercase;
  letter-spacing: 0.12em;
  font-size: 12px;
  color: #5f7895;
}

h1,
h2 {
  margin: 8px 0 0;
  color: #18304c;
}

.copy {
  margin: 12px 0 0;
  color: #4d627b;
  line-height: 1.5;
}

.card-head {
  margin-bottom: 16px;
}

.account-overview {
  background:
    radial-gradient(circle at top right, rgba(255, 179, 71, 0.18), transparent 34%),
    linear-gradient(180deg, rgba(255, 255, 255, 0.98), rgba(244, 249, 255, 0.98));
}

.account-chips {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  margin-top: 18px;
}

.account-chip {
  display: inline-flex;
  align-items: center;
  padding: 8px 12px;
  border-radius: 999px;
  background: #eef4fb;
  color: #35506d;
  font-size: 0.85rem;
  font-weight: 600;
}

.profile-form {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 14px;
}

.profile-form label {
  display: grid;
  gap: 6px;
  color: #35506d;
  font-weight: 600;
}

.profile-form input {
  border: 1px solid #d3dfec;
  border-radius: 12px;
  padding: 12px 14px;
  background: #fff;
}

.profile-form button,
.ghost-button {
  border: 0;
  border-radius: 12px;
  padding: 12px 16px;
  background: #132238;
  color: #f5f9ff;
  font-weight: 700;
  cursor: pointer;
}

.profile-form button {
  grid-column: 1 / -1;
}

.ghost-button {
  background: #35506d;
}

.error,
.success {
  margin: 0 0 14px;
  padding: 12px 14px;
  border-radius: 12px;
}

.error {
  background: #ffe6e2;
  color: #8a2f25;
}

.success {
  background: #edf8f3;
  color: #1f5e3b;
}

@media (max-width: 720px) {
  .profile-header {
    flex-direction: column;
  }

  .profile-form {
    grid-template-columns: 1fr;
  }
}
</style>
