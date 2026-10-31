# 👑 RoyalTab — Custom Tablist Plugin for Royal SMP (100% Standalone)

A lightweight, custom Tablist plugin built specifically for **Paper 1.21.x (Java 21)**. It faithfully replicates the **CraftedCroix / Crafted SMP** tab list aesthetic using **purely native Minecraft characters and MiniMessage gradients** — **NO client resource packs, texture packs, or server prompts required!**

---

## 📸 Native Visual Features (Zero Resource Pack)

* **3D Cyan Server Header**: "ROYAL SMP" in bold cyan `#00FDFB` with deep 3D drop-shadow extrusion (`#008F8E` / `#005577`) and `[ S M P ]` block art that renders on all standard Minecraft clients.
* **4 Built-in Logo Styles**: Toggle anytime in `config.yml`:
  * `3D_BLOCK` (Default): 3D isometric block logo matching the reference image.
  * `3D_TALL`: Extra tall 3-line 3D block text.
  * `SMALL_CAPS`: Modern, clean small-caps cyan gradient.
  * `ROYAL_CROWN`: Gold crown & cyan royal banner.
* **Top & Bottom Divider Bars**: Clean dark-gray horizontal separator lines (`&8&m                     &r`).
* **Live Online Counter**: `<gray>ONLINE PLAYERS: <green>%online%</green></gray>`.
* **Bordered Rank Badges & Formatted Names**:
  * Founder: `<dark_red>[</dark_red><red><bold>FOUNDER</bold></red><dark_red>]</dark_red> <red>%player%</red>`
  * Admin: `<dark_orange>[</dark_orange><gold><bold>ADMIN</bold></gold><dark_orange>]</dark_orange> <gold>%player%</gold>`
  * Mod: `<dark_green>[</dark_green><green><bold>MOD</bold></green><dark_green>]</dark_green> <green>%player%</green>`
  * VIP: `<dark_yellow>[</dark_yellow><yellow><bold>VIP</bold></yellow><dark_yellow>]</dark_yellow> <yellow>%player%</yellow>`
  * Member: `<gray>[MEMBER]</gray> <white>%player%</white>`
* **Scoreboard Team Sorting**: Ranks are sorted in priority order so Founders and Staff appear at the very top of the tab list.
* **Dynamic Ping Latency**: Shows green, yellow, or red signal bars (`📶`) based on the player's connection latency.
* **Server Footer Links**:
  * `WEBSTORE: ROYALSMP.TEBEX.IO`
  * `DISCORD: DSC.GG/ROYALSMP`
* **Zero Lag**: Runs on an asynchronous scheduler interval (default 20 ticks / 1 second) with Paper Adventure API Components.
* **Soft Hook Support**: Integrates automatically with **PlaceholderAPI** and **LuckPerms** (with built-in fallbacks if they aren't installed).

---

## 🚀 How to Build the JAR File on GitHub

You can build the plugin `.jar` file directly on GitHub using the included **GitHub Actions workflow**:

1. **Create a new GitHub Repository**:
   * Go to [GitHub](https://github.com/new) and create a repository (e.g., `RoyalTab`).
2. **Push the Project Files**:
   ```bash
   git init
   git add .
   git commit -m "Initial commit for RoyalTab plugin"
   git branch -M main
   git remote add origin https://github.com/YOUR_USERNAME/RoyalTab.git
   git push -u origin main
   ```
3. **Download the Compiled JAR**:
   * In your GitHub repository, click the **Actions** tab at the top.
   * Select the workflow **"Build RoyalTab Plugin JAR"**.
   * Once it completes (~30–45 seconds), click on the build run.
   * Under the **Artifacts** section at the bottom, download **`RoyalTab-1.21-JAR`**.
   * Extract the zip to get `RoyalTab-1.0.0.jar`!

---

## 💻 Server Installation (Paper 1.21.x)

1. Put `RoyalTab-1.0.0.jar` into your server's `plugins/` directory.
2. Restart or start your server.
3. The plugin generates its configuration at `plugins/RoyalTab/config.yml`.
4. Run `/royaltab reload` anytime you make changes.

---

## ⚙️ Commands & Permissions

| Command | Permission | Description |
| :--- | :--- | :--- |
| `/royaltab reload` | `royaltab.admin` | Reloads configuration and refreshes tablist |
| N/A | `royaltab.founder` | Assigns Founder tablist formatting (if not using LuckPerms) |
| N/A | `royaltab.admin_rank` | Assigns Admin tablist formatting |
| N/A | `royaltab.mod_rank` | Assigns Mod tablist formatting |
| N/A | `royaltab.vip_rank` | Assigns VIP tablist formatting |
