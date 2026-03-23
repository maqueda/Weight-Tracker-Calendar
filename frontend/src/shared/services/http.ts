const API_BASE_URL = import.meta.env.VITE_API_BASE_URL ?? "http://localhost:8080/api/v1";

export async function http<T>(path: string, init?: RequestInit): Promise<T> {
  const response = await fetch(`${API_BASE_URL}${path}`, {
    headers: {
      "Content-Type": "application/json",
      ...(init?.headers ?? {})
    },
    ...init
  });

  if (!response.ok) {
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
