use constant {
    BASE_LANG => "en_US",
    OUTPUT_FILE => "translation-diff.diff"
};

my $lang = $ARGV[0] or die("Missing language to diff with");

sub lang_file {
    my ($lang) = @_;
    my $filename = "src/main/resources/assets/refinedstorage/lang/" . $lang . ".lang";
    open(my $fh, $filename) or die("Couldn't open $filename");
    return $fh;
}

sub write_keys {
    my ($lang, $lang_keys) = @_;
    my $filename = $lang . ".tmp";
    open(my $fh, '>', $filename) or die("Couldn't open temp file $filename for $lang for writing");
    print $fh $lang_keys;
    close $fh;
    return $filename;
}

sub lang_keys {
    my ($lang_file) = @_;
    my $keys = "";
    while (my $line = <$lang_file>) {
        my @p = split("=", $line);
        $keys .= $p[0] . "\n";
    }
    return $keys;
}

my $base_keys = lang_keys(lang_file(BASE_LANG));
my $t1 = write_keys(BASE_LANG, $base_keys);

my $lang_keys = lang_keys(lang_file($lang));
my $t2 = write_keys($lang, $lang_keys);

unlink(OUTPUT_FILE);

system("diff -s -y $t1 $t2 > " . OUTPUT_FILE);

unlink($t1, $t2);