{
  lib,
  maven,
}:

maven.buildMavenPackage rec {
  pname = "luacraft";
  version = "1.2.0";

  src = ./.;
  mvnHash = "sha256-9J69HF+QjkCsKrORnRiE7WqdEwAMuMIZPRuQhPXMWjw=";

  installPhase = ''
    runHook preInstall
    install -Dm600 target/LuaCraft-${version}.jar -t $out/bin/
    runHook postInstall
  '';

  meta = {
    description = "Execute Lua scripts on your Minecraft server. This is very experimental.";
    changelog = "https://github.com/shawnjb/LuaCraft/releases/tag/${version}";
    homepage = "https://github.com/shawnjb/LuaCraft";
    license = lib.licenses.asl20;
  };
}
