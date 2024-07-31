require "formula"

class @name@ < Formula
  desc '@description@'
  homepage '@homepage@'
  url "@asseturl@"
  sha256 "@assethash@"
  version '@version@'

  depends_on 'openjdk@17'

  def install
    jreRuntimePath = Formula["openjdk@17"].opt_prefix + "/libexec/openjdk.jdk/Contents/Home"
    system "./mvnw", "-Pmac-noarch" "package", "-DskipTests" "-DjreRuntimePath"+jreRuntimePath
    (prefix/"Applications").install "target/GitHub Actions Notifier.app"
  end
end