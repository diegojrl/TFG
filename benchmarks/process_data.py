import csv


def parse_metrics(path):
    cpu_total = 0.0
    cpu_count = 0
    max_ram = 0
    max_threads = 0
    csvfile = open(path)
    reader = csv.DictReader(csvfile)
    for row in reader:
        cpu = float(row["CPU usage [%]"].strip('"'))
        used_b = int(row["Used [B]"].replace(',', ''))
        live = int(row["Live"])
        daemon = int(row["Daemon"])
        cpu_total += cpu
        cpu_count += 1
        max_ram = max(max_ram, used_b)
        max_threads = max(max_threads, live + daemon)
    return (cpu_total / cpu_count, max_ram / (1024*1024), max_threads)


data_folder = './data/'
extensions = ["allow-all-extension", "trust-extension"]
tests = [ "20", "100", "500" ]



for test in tests:
    output = open("./data/"+test+".csv", mode='w', newline='')
    writer = csv.DictWriter(output, fieldnames=["extension", "avg publish count", "avg cpu", "max ram(MB)", "max threads"])
    writer.writeheader()
    for extension in extensions:
        (cpu, ram, threads) = (0, 0, 0)
        main_csv = open(data_folder + extension + '/' +test+".csv") 
        reader = csv.DictReader(main_csv)
        skip = True
        n = 1
        
        for row in reader:
            publish_count = int(row["publish_count"])
            if skip:    #Skip first line
                skip = False
                first_publish = publish_count
                continue
            (cpu1, ram1, threads1) = parse_metrics(data_folder + extension + '/' +test+"_"+str(n)+".csv")
            cpu += cpu1
            ram = max(ram, ram1)
            threads = max(threads, threads1)
            last_publish_count = publish_count
    
        writer.writerow({'extension': extension, 
                         'avg publish count': "{:.2f}".format((last_publish_count - first_publish) / 3),
                         'avg cpu': "{:.2f}".format(cpu / 3),
                         'max ram(MB)': "{:.2f}".format(ram),
                         'max threads': threads
                        })
        
