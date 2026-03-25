<template>
  <main class="profile-page">
    <section class="profile-wrapper">
      <ProfilePanel />
    </section>
  </main>
</template>

<script setup lang="ts">
import ProfilePanel from "../components/ProfilePanel.vue";
</script>

<style scoped>
.profile-page {
  min-height: 100vh;
  padding: 32px;
}

.profile-wrapper {
  width: min(100%, 880px);
  margin: 0 auto;
}

@media (max-width: 720px) {
  .profile-page {
    padding: 18px;
  }
}
</style>
