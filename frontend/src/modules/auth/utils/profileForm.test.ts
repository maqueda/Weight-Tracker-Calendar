import { describe, expect, it } from "vitest";
import { buildProfileFormState, toUpdateProfileInput } from "./profileForm";

describe("profileForm", () => {
  it("builds editable fields from the authenticated user", () => {
    const form = buildProfileFormState({
      id: 1,
      username: "juanmaqueda",
      displayName: "Juan Maqueda Vargas",
      firstName: "Juan",
      lastName: "Maqueda Vargas",
      email: "juan@example.com",
      timezone: "Europe/Madrid"
    });

    expect(form).toEqual({
      username: "juanmaqueda",
      firstName: "Juan",
      lastName: "Maqueda Vargas",
      email: "juan@example.com"
    });
  });

  it("normalizes blank email before sending the update", () => {
    expect(
      toUpdateProfileInput({
        username: " juanmaqueda ",
        firstName: " Juan ",
        lastName: " Vargas ",
        email: "   "
      })
    ).toEqual({
      username: "juanmaqueda",
      firstName: "Juan",
      lastName: "Vargas",
      email: undefined
    });
  });
});
