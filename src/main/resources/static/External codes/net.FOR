        dimension nffo(20),nfft(20),rff(20),qff(20),preg(20) 
        dimension no(170),nt(170),r(170),c(170),q(170),qold(170),fb(170)
        dimension xflo(80),kp(81),ja(230),jb(230),in(230)
	  dimension a(10)
        common /march/ b(80,80),h(80),rhs(80)
c
        open (unit=5,file='inp.txt')
	  open (unit=6,file='oup.txt')
	  read (5,*) nff,nfb,nn,iter,err
        do 5 i = 1,nn
        h(i) = 0.
        xflo(i) = 0.
  5     continue
        if(nff.ne.0) then
        do 10 i = 1,nff
        read(5,*) nffo(i),nfft(i),rff(i),qff(i)
        xflo(nffo(i)) = xflo(nffo(i))-qff(i)
        xflo(nfft(i)) = xflo(nfft(i))+qff(i)
  10    continue
        else
        endif
        if(nfb.ne.0) then
        do 15 i = 1,nfb
        read (5,*) no(i),nt(i),r1,a(i),fb(i)
        r(i) = r1-a(i)
        qold(i) = 0.
  15    continue
        else
        endif
        nbn = nfb
  20    nbn = nbn+1
        read (5,*,iostat = jan) no(nbn),nt(nbn),r(nbn)
        if(jan.eq.0) then
        fb(nbn) = 0.
        qold(nbn) = 0.
        goto 20
        else
        endif
        nbn = nbn-1
        kp(1) = 1
        k = 0
        j = 1
  25    continue
        do 30 l = 1,nbn
        if (no(l).eq.j) then
        
        k = k+1
        ja(k) = nt(l)
        jb(k) = 1
        in(k) = l
        else if (nt(l).eq.j) then
        k = k+1
        ja(k) = no(l)
        jb(k) = -1
        in(k) = l
        else
        endif
  30    continue
        j = j+1
        kp(j) = k+1
        if(j.le.nn) goto 25
c
        do 35 itr = 1,iter
        if(itr.eq.1) then
        do 40 j = 1,nbn
        c(j) = 1./r(j)
  40    continue
        else if (itr.eq.2) then
        do 45 j = 1,nbn
        qab= abs(q(j))
        qold(j) = q(j)
        c(j) = 1./(qab*r(j))
  45    continue
        else
        do 50 j = 1,nbn
        qa = sqrt(abs(qold(j)*q(j)))
        c(j) = 1./(qa*r(j))
        qold(j) = qa*q(j)/abs(q(j))
  50    continue
        endif
        do 55 i = 1,nn
        sm = 0.
        su = 0.
        do 60 j = 1,nn
        b(i,j) = 0.
  60    continue
        kpa = kp(i)
        kpb = kp(i+1)-1
        do 65 m = kpa,kpb
        sm = sm+c(in(m))
        su = su + jb(m)*c(in(m))*fb(in(m))
        b(i,ja(m)) = -c(in(m))
  65    continue
        rhs(i) = xflo(i)-su
        b(i,i) = sm
  55    continue
        b(1,1) = 1
        rhs(1) = 0.
        do 70 j = 2,nn
        b(1,j) = 0.
        b(j,1) = 0.
  70    continue
        call choles(nn)
        do 75 l =1,nbn
        q(l) = c(l)*((h(no(l))-h(nt(l)))+fb(l))
  75    continue
        do 80 j = 1,nbn
        if(abs(qold(j)-q(j)).gt.err) goto 35
  80    continue
        write (6,*) ' '
        write(6,*) '------------------------------------------'
        write (6,*) 'solution obtained at iteration no ',itr
        write (6,*) '-----------------------------------------'
        write (6,*) 'br.no org.n ter.n     p(o)      p(t)     flow   
	+  resist       flow'
        write (6,*) '                      (Pa)      (Pa)    (m3/s)   
     +  (Ns2/m8)     (m3/min)'

	  write (6,*) ' '
        do 83 is = 1,nbn 
        qold(is) = q(is)*60
	  if (is.le.nfb) r(is) = r(is)+a(is)
	  write (6,33) is,no(is),nt(is),h(no(is)),h(nt(is)),
     +q(is),r(is),qold(is) 	  
  33    format(/,i3,4x,i2,4x,i2,4x,f7.1,4x,f7.1,4x,f6.2,4x,
     +  f9.5,4x,f8.1)
  	   83    continue
        write(6,*) ' '
        if(nff.ne.0) then
        write(6,*) 'for fixed flow branches reg(+)/booster(-) values'
        write(6,*) '------------------------------------------------'
        write(6,*) 'br    org.n   ter.n   flow (m3/s) reg/boo (Pa) 
     +  regarea (m2)'
        write (6,*) ' '
        do 85 m = 1,nff
        preg(m) = h(nffo(m))-h(nfft(m))-rff(m)*qff(m)*qff(m)
        if(preg(m).gt.0) then
        ru = preg(m)/(qff(m)*qff(m))
        rff(m) = 1.2/sqrt(ru)
        else
        rff(m) = 0.0
        endif
        write(6,34) m,nffo(m),nfft(m),qff(m),preg(m),rff(m)
  34    format(/,i3,4x,i3,4x,i3,5x,f7.2,7x,f7.1,12x,f5.2)
  85    continue
        else
        endif
        write(6,*) ' '
        write(6,*) '---------------------------------------------------'
        stop
  35    continue
        write(6,*) 'iterations exceed ',iter
        close (unit=5)
	  close (unit=6)
	  stop
        end
c
        subroutine choles(n)
        common /march/ d(80,80),x(80),z(80)
        dimension y(80)
        do 5 i = 1,n
        x(i) = 0.
        y(i) = 0.
  5     continue
        d(1,1) = sqrt(d(1,1))
        do 10 j = 2,n
        d(j,1) = d(j,1)/d(1,1)
  10    continue
        do 15 i = 2,n-1
        sum = 0.
        do 20 k = 1,i-1
        sum = sum+d(i,k)*d(i,k)
  20    continue
        d(i,i) = sqrt(d(i,i)-sum)
        do 15 j = i+1,n
        sum = 0.
        do 25 k = 1,i-1
        sum = sum+d(j,k)*d(i,k)
  25    continue
        d(j,i) = (d(j,i)-sum)/d(i,i)
  15    continue
        sum = 0.
        do 30 k = 1,n-1
        sum = sum+d(n,k)*d(n,k)
  30    continue
        d(n,n) = sqrt(d(n,n)-sum)
        y(1) = z(1)/d(1,1)
        do 35 i = 2,n
        sum = 0.
        do 40 j = 1,i-1
        sum = sum+d(i,j)*y(j)
  40    continue
        y(i) = (z(i)-sum)/d(i,i)
  35    continue
        x(n) = y(n)/d(n,n)
        do 45 i = n-1,1,-1
        sum = 0.
        do 50 j = i+1,n
        sum = sum+d(j,i)*x(j)
  50    continue
        x(i) = (y(i)-sum)/d(i,i)
  45    continue
        return
        end

