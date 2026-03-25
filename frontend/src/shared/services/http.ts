import { clearAuthToken, getAuthToken } from "../../modules/auth/services/authStorage";

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL ?? "http://localhost:8080/api/v1";

// Cliente HTTP mínimo compartido por toda la app. Añade el token cuando existe
// y fuerza volver al login si la sesión deja de ser válida.
export async function http<T>(path: string, init?: RequestInit): Promise<T> {
  const token = getAuthToken();
  const response = await fetch(`${API_BASE_URL}${path}`, {
    headers: {
      "Content-Type": "application/json",
      ...(token ? { Authorization: `Bearer ${token}` } : {}),
      ...(init?.headers ?? {})
    },
    ...init
  });

  if (!response.ok) {
    if (response.status === 401) {
      clearAuthToken();
      if (window.location.pathname !== "/auth") {
        window.location.href = "/auth";
      }
    }

    const fallback = "No se pudo completar la solicitud.";
    let message = fallback;
    try {
      const data = (await response.json()) as { message?: string };
      message = data.message ?? fallback;
    } catch {
      message = fallback;
    }
    throw new Error(message);
  }

  return response.json() as Promise<T>;
}
