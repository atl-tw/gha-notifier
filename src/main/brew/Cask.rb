cask "@artifactId@" do
  version "@version@"
  name "@name@"
  homepage "@homepage@"
  app "@name@.app"

  if Hardware::CPU.arm?
    url "@armasseturl@"
    sha256 "@armassethash@"
  else
    url "@x86asseturl@"
    sha256 "@x86assethash@"
  end

  postflight do
    system_command "xattr",
                   args: ["-d", "com.apple.quarantine", "#{appdir}/@name@.app"],
                   sudo: true
  end

end