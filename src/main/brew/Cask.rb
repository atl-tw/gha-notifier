cask "@artifactId@" do
  version "@version@"

  # Define URLs for different architectures
  if Hardware::CPU.arm?
    url "@armasseturl@"
    sha256 "@armassethash@"
  else
    url "@x86asseturl@"
    sha256 :"@x86assethash@"
  end

  name "@name@""
  homepage "@homepage@" # Replace with the application's homepage

  app "@name@.app"
  postflight do
    system_command "xattr",
                   args: ["-d", "com.apple.quarantine", "#{appdir}/@name@.app"],
                   sudo: true
  end

end