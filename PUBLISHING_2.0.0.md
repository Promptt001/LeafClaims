# Publishing LeafClaims 2.0.0

These are the steps to run yourself, with your own GitHub credentials. Nothing has been pushed
from the sandbox, and the staging copy has no remote configured.

## What you are publishing

`/home/user/02_sandbox/01_HuskClaims/LeafClaims` is a complete, independent repository:

- one commit (`LeafClaims 2.0.0`) on branch `main`
- one annotated tag, `2.0.0`, on that commit
- 572 tracked files, roughly 5 MB
- no history from `HuskClaimsPlus`, so none of the HuskClaimsPlus commit log becomes public

The four release jars are built from this commit during staging. They are in
`target/`, next to a regenerated `SHA256SUMS.txt`.

## 1. Confirm the staging copy is intact

Build the jars from a **clean** tree. The version string comes from `git describe`, so an
uncommitted edit makes it `2.0.0-dirty`, and Gradle then writes `-indev` jars instead of the
release-named ones. The release jars already in `target/` are left stale and nothing warns you.
Commit or amend first, confirm `git status --porcelain` is empty and `git describe --tags` prints
plain `2.0.0`, then build.

```bash
cd /home/user/02_sandbox/01_HuskClaims/LeafClaims
git log --oneline          # expect exactly one line: "LeafClaims 2.0.0"
git tag                    # expect: 2.0.0
git status --short         # expect no output
git ls-files | wc -l       # expect 578 (573 source/doc files + 5 README screenshots)
git ls-files images       # expect the 5 README screenshots, all committed
```

## 2. Create the empty GitHub repository

Create `Promptt001/LeafClaims` on GitHub with **no** README, licence or `.gitignore`. An empty
repository keeps the single-commit history clean; adding a file in the web interface creates a
second commit you would then have to merge.

## 3. Push the single commit and the tag

```bash
cd /home/user/02_sandbox/01_HuskClaims/LeafClaims
git remote add origin git@github.com:Promptt001/LeafClaims.git
git push -u origin main
git push origin 2.0.0
```

## 4. Create the release

Using the GitHub CLI:

```bash
cd /home/user/02_sandbox/01_HuskClaims/LeafClaims
gh release create 2.0.0 \
  --title "LeafClaims 2.0.0" \
  --notes-file RELEASE_NOTES_2.0.0.md \
  "target/LeafClaims-Paper-2.0.0.jar" \
  "target/LeafClaims-Bukkit-2.0.0.jar" \
  "target/LeafClaims-Fabric-2.0.0+mc.26.2.jar" \
  "target/LeafClaims-Fabric-2.0.0+mc.26.3.jar" \
  "target/SHA256SUMS.txt"
```

Quote the Fabric filenames: `+` has meaning in some shells.

Without the CLI, create the release in the web interface, paste the contents of
`RELEASE_NOTES_2.0.0.md`, and upload those five files.

## 5. Check the published files

Download what you uploaded and check it, rather than checking the local copies:

```bash
cd /path/to/downloaded
sha256sum -c SHA256SUMS.txt
```

## Notes

- **The Fabric jars are not reproducible.** The build sometimes writes `Multi-Release: true`
  into the nested jar manifest and sometimes does not, which changes the hash without changing
  code. If you rebuild, regenerate `SHA256SUMS.txt` with
  `cd target && sha256sum LeafClaims-Paper-2.0.0.jar LeafClaims-Bukkit-2.0.0.jar 'LeafClaims-Fabric-2.0.0+mc.26.2.jar' 'LeafClaims-Fabric-2.0.0+mc.26.3.jar' > SHA256SUMS.txt`
  — list the four release jars by name. A plain `LeafClaims-*.jar` glob also picks up the
  `-sources` and `-javadoc` jars, which are not uploaded, and then every `sha256sum -c` on the
  release page reports them as missing.
  and re-upload rather than reusing the old file.
- **Do not upload anything from the old `target/` directory** in `HuskClaimsPlus`. It holds
  earlier builds whose filenames carry a commit hash and `-indev`, and they are not this
  release.
- The tag is `2.0.0` without a `v`. Keep it that way: the in-plugin update checker reads plain
  version tags.
- `PUBLISHING_2.0.0.md` and `RELEASE_NOTES_2.0.0.md` are both in the repository. They are
  ordinary documentation files, and the release notes are also the body of the GitHub release.
  Delete `PUBLISHING_2.0.0.md` from the repository afterwards if you would rather not publish
  your own release runbook.
