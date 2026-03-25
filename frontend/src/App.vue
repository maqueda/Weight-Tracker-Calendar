<template>
  <div v-if="!authStore.initialized" class="boot-screen">Recuperando sesión...</div>
  <router-view v-else />
</template>

<script setup lang="ts">
import { onMounted } from "vue";
import { useAuthStore } from "./modules/auth/store/useAuthStore";

const authStore = useAuthStore();

onMounted(() => {
  authStore.restoreSession();
});
</script>

<style scoped>
.boot-screen { min-height: 100vh; display: grid; place-items: center; color: #35506d; font-weight: 600; }
</style>
