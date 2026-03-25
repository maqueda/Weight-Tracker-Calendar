import type { CalendarDay } from "../types/calendar";

export function buildWeightEntriesCsv(days: CalendarDay[]): string {
  const lines = [
    ["fecha", "dia_semana", "peso_kg", "notas"],
    ...days
      .filter((day) => day.weightKg !== null)
      .map((day) => [
        day.date,
        day.dayOfWeek,
        day.weightKg?.toFixed(1) ?? "",
        escapeCsvField(day.notes ?? "")
      ])
  ];

  return lines.map((line) => line.join(",")).join("\n");
}

export function downloadCsvFile(content: string, filename: string): void {
  const blob = new Blob([content], { type: "text/csv;charset=utf-8;" });
  const url = URL.createObjectURL(blob);
  const link = document.createElement("a");

  link.href = url;
  link.download = filename;
  link.click();

  URL.revokeObjectURL(url);
}

function escapeCsvField(value: string): string {
  if (value.includes(",") || value.includes("\"") || value.includes("\n")) {
    return `"${value.split("\"").join("\"\"")}"`;
  }
  return value;
}
